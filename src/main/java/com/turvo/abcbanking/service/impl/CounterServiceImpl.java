package com.turvo.abcbanking.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.model.Branch;
import com.turvo.abcbanking.model.Counter;
import com.turvo.abcbanking.model.CounterXServiceStep;
import com.turvo.abcbanking.model.ServiceStep;
import com.turvo.abcbanking.model.Token;
import com.turvo.abcbanking.model.TokenWorkflow;
import com.turvo.abcbanking.model.User;
import com.turvo.abcbanking.repository.CounterRepository;
import com.turvo.abcbanking.repository.CounterXServiceStepRepository;
import com.turvo.abcbanking.repository.ServiceStepRepository;
import com.turvo.abcbanking.repository.TokenRepository;
import com.turvo.abcbanking.repository.TokenWorkflowRepository;
import com.turvo.abcbanking.service.BranchService;
import com.turvo.abcbanking.service.CounterService;
import com.turvo.abcbanking.service.UserService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Service implementation for Counter operations
 * 
 * @author Prabal Ghura
 *
 */
@Service("counterService")
public class CounterServiceImpl extends BaseServiceImpl implements CounterService {

	@Autowired
	BranchService branchService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	CounterRepository counterRepository;
	
	@Autowired
	TokenRepository tokenRepository;
	
	@Autowired
	TokenWorkflowRepository tokenWorkflowRepository;
	
	@Autowired
	ServiceStepRepository serviceStepRepository;
	
	@Autowired
	CounterXServiceStepRepository counterXServiceStepRepository;
	
	/**
	 * Fetches all counters in branch from DB and fetches all internal components using breadth first search approach
	 */
	@Override
	public List<Counter> getBranchCountersFromDB(Long branchId) {
		List<Counter> counterList = counterRepository.findByBranchId(branchId);
		List<Counter> resultList = new ArrayList<>();
		
		counterList.forEach(counter -> resultList.add(getCounterFull(counter)));
		
		return resultList;
	}
	
	/**
	 * Fetches services steps for a counter from DB
	 * Fetches tokens assigned to a counter from DB
	 * tokens are pushed to counter queue in order of retrieval
	 * 
	 * If branch is refreshed from DB again, it is possible that tokens are not added to counter queue in the same order as it was before
	 * DB doesn't maintain counter queue order. This is done intentionally to provide asynchronous DB calls.
	 * However in normal scenario counter will not be fetched again & again from DB that's why maintaining queue only at JVM cache level
	 * will solve the purpose
	 * 
	 * @param counter
	 * @return
	 */
	private Counter getCounterFull(Counter counter) {
		counter.setSteps(serviceStepRepository.findByCounterId(counter.getId()));
		List<Token> tokens = tokenRepository.getTokensForCounter(counter.getId());
		tokens.forEach(counter::addToken);
		
		return counter;
	}

	/**
	 * Branch Id is validated
	 * Access is checked
	 * Passed counter instance is validated
	 * 
	 * counter is persisted in DB and synchronously updated in JVM cache.
	 */
	@Override
	@Transactional(readOnly = false)
	public Counter createNewCounter(String creatorId, Long branchId, Counter counter) {
		Branch branch =  getBranch(branchId);
		checkAccess(creatorId, branch);
		getOperator(counter.getCurrentOperator());
		counter.setBranchId(branchId);
		counter.setNumber(counterRepository.getMaxCounterNumber(branchId) + 1);
		counter.setLastModifiedBy(creatorId);
		counter = getCounterFull(counterRepository.saveAndFlush(counter));
		
		branchService.updateCounter(counter);
		return counter;
	}

	/**
	 * Inputs are validated
	 * Access is checked
	 * 
	 * counter is updated in DB and synchronously updated in JVM cache.
	 */
	@Override
	@Transactional(readOnly = false)
	public Counter assignOperator(String assignerId, Long branchId, Integer counterNumber, String operatorId) {
		Branch branch =  getBranch(branchId);
		checkAccess(assignerId, branch);
		getOperator(operatorId);
		Counter counter = getCounter(branchId, counterNumber);
		counter.setCurrentOperator(operatorId);
		counter.setLastModifiedBy(assignerId);
		counter = getCounterFull(counterRepository.saveAndFlush(counter));
		
		branchService.updateCounter(counter);
		return counter;
	}

	/**
	 * Inputs are validated
	 * Access is checked
	 * 
	 * counter steps mapping is synchronously updated in DB.
	 * 
	 * Entire branch is flushed from cache and re-fetched from DB. Because this change might result in change of services branch supports
	 * 
	 * Counter is fetched from updated JVM cache branch and returned
	 */
	@Override
	@Transactional(readOnly = false)
	public Counter assignSteps(String assignerId, Long branchId, Integer counterNumber, List<ServiceStep> steps) {
		Branch branch =  getBranch(branchId);
		checkAccess(assignerId, branch);
		Counter counter = getCounter(branchId, counterNumber);
		List<Long> stepIds = getStepIdstoBeAdded(counter.getId(), steps);
		
		List<CounterXServiceStep> toBeAssignedSteps = new ArrayList<>();
		stepIds.forEach(step -> toBeAssignedSteps.add(new CounterXServiceStep(counter.getId(), step, assignerId)));
		
		counterXServiceStepRepository.save(toBeAssignedSteps);
		counterXServiceStepRepository.flush();
		
		branch = branchService.updateBranch(branch.getManagerId(), branchId);
		
		return branch.getCounter(counterNumber);
	}
	
	/**
	 * This is 1 of 3 frequent operations in entire application which involves DB update
	 * 
	 * Inputs are validated
	 * Operator access is checked
	 * First token in counter queue is polled and assigned counter is updated in cache
	 * 
	 * Token workflow is updated
	 * If next step is required token is added to counter queue of next best counter in branch
	 * New counter is updated in cache
	 * 
	 */
	@Override
	@Transactional(readOnly = false)
	public void serviceFirstCounter(String executorId, Long branchId, Integer counterNumber, String comments) {
		Counter counter = getCounter(branchId, counterNumber);
		checkOperatorAccess(executorId, counter);
		Token token = counter.pullToken();
		
		List<TokenWorkflow> steps1 = token.serviceAndGetNextPendingWorkFlowStep(comments, counter.getCurrentOperator());
		
		Counter nextCounter = null;
		if(steps1.size()>1) {
			Long stepId = steps1.get(1).getStepId();
			nextCounter = branchService.getBestCounter(branchId, token.getType(), stepId);
			steps1.get(1).setCounterId(nextCounter.getId());
			nextCounter.addToken(token);
			
		}
		tokenWorkflowRepository.save(steps1);
		tokenWorkflowRepository.flush();
		branchService.updateCounter(counter);
		if(steps1.size()>1) {
			branchService.updateCounter(nextCounter);			
		}
	}
	
	/**
	 * Service steps are validated for null entries
	 * Step Ids are validated for their presence in DB
	 * 
	 * Existing step ids for the specified counter which already exist in DB are filtered out
	 * 
	 * @param counterId
	 * @param steps
	 * @return New valid step ids for counter
	 */
	private List<Long> getStepIdstoBeAdded(Long counterId, List<ServiceStep> steps) {
		steps.forEach(step -> {
			if(Objects.isNull(step.getId()))
				throw new BusinessRuntimeException(ApplicationConstants.ERR_INVALID_SERVICE_STEP_ID);
		});
		Set<Long> stepIds = steps.stream().map(ServiceStep::getId).collect(Collectors.toSet());
		List<Long> stepsIdList = new ArrayList<>(stepIds);
		
		List<ServiceStep> dbSteps = serviceStepRepository.findByIdIn(stepsIdList);
		
		if(dbSteps.size() != stepsIdList.size())
			throw new BusinessRuntimeException(ApplicationConstants.ERR_SERVICE_STEP_NOT_EXIST);
		
		List<CounterXServiceStep> existingSteps = counterXServiceStepRepository.findByCounterIdAndStepIdIn(counterId, stepsIdList);
		
		final Set<Long> stepIds1 = existingSteps.stream().map(CounterXServiceStep::getStepId).collect(Collectors.toSet());
		
		return stepsIdList.stream().filter(id -> !stepIds1.contains(id)).collect(Collectors.toList());
	}
	
	/**
	 * counter operator id is matched against passed userId, exception is thrown if they don't match
	 * 
	 * @param userId
	 * @param counter
	 */
	private void checkOperatorAccess(String userId, Counter counter) {
		if(!counter.getCurrentOperator().equals(userId))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_ACCESS_DENIED);
	}
	
	/**
	 * A user id is matched against DB records, if there is no match exception is thrown
	 * 
	 * @param operatorId
	 * @return
	 */
	private User getOperator(String operatorId) {
		User operator = userService.getUser(operatorId);
		if(!roleService.checkAccessForUser(operatorId, ApplicationConstants.ROLE_OPERATOR))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_OPERATOR_NOT_EXIST);
		return operator;
	}
	
	/**
	 * branch manager id is matched against passed userId, exception is thrown if they don't match
	 * 
	 * @param userId
	 * @param branch
	 */
	private void checkAccess(String userId, Branch branch) {
		if(!branch.getManagerId().equals(userId))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_ACCESS_DENIED);
	}
	
	/**
	 * branch is fetched from JVM cache against passed branchId, exception is thrown if no such branch exists in cache
	 * 
	 * @param branchId
	 * @return branch instance if existing
	 */
	private Branch getBranch(Long branchId) {
		Branch branch =  branchService.getBranch(branchId);
		if(Objects.isNull(branch))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		return branch;
	}
	
	/**
	 * counter is fetched from branch instance retrieved from JVM cache, exception is thrown if found none
	 * 
	 * @param branchId
	 * @param counterNumber
	 * @return counter instance if existing
	 */
	private Counter getCounter(Long branchId, Integer counterNumber) {
		Counter counter = getBranch(branchId).getCounter(counterNumber);
		if(Objects.isNull(counter))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_COUNTER_NOT_EXIST);
		return counter;
	}
}

