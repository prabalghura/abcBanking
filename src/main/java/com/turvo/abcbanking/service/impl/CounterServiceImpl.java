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
	
	@Override
	public List<Counter> getBranchCountersFromDB(Long branchId) {
		List<Counter> counterList = counterRepository.findByBranchId(branchId);
		List<Counter> resultList = new ArrayList<>();
		
		counterList.forEach(counter -> resultList.add(getCounterFull(counter)));
		
		return resultList;
	}
	
	private Counter getCounterFull(Counter counter) {
		counter.setSteps(serviceStepRepository.findByCounterId(counter.getId()));
		List<Token> tokens = tokenRepository.getTokensForCounter(counter.getId());
		tokens.forEach(counter::addToken);
		
		return counter;
	}

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
	
	@Override
	@Transactional(readOnly = false)
	public void serviceFirstCounter(String executorId, Long branchId, Integer counterNumber, String comments) {
		Counter counter = branchService.getBranch(branchId).getCounter(counterNumber);
		checkOperatorAccess(executorId, counter);
		Token token = counter.pullToken();
		branchService.updateCounter(counter);
		
		List<TokenWorkflow> steps1 = token.serviceAndGetNextPendingWorkFlowStep(comments, counter.getCurrentOperator());
		
		if(steps1.size()>1) {
			Long stepId = steps1.get(1).getStepId();
			Counter nextCounter = branchService.getBestCounter(branchId, token.getType(), stepId);
			steps1.get(1).setCounterId(nextCounter.getId());
			nextCounter.addToken(token);
			
			branchService.updateCounter(nextCounter);
		}
		tokenWorkflowRepository.save(steps1);
		tokenWorkflowRepository.flush();
	}
	
	private List<Long> getStepIdstoBeAdded(Long counterId, List<ServiceStep> steps) {
		steps.forEach(step -> {
			if(Objects.isNull(step.getId()))
				throw new BusinessRuntimeException(ApplicationConstants.ERR_INVALID_SERVICE_STEP_ID);
		});
		Set<Long> stepIds = steps.stream().map(ServiceStep::getId).collect(Collectors.toSet());
		List<Long> stepsIdList = new ArrayList<>();
		stepIds.forEach(stepsIdList::add);
		
		List<ServiceStep> dbSteps = serviceStepRepository.findByIdIn(stepsIdList);
		
		if(dbSteps.size() != stepsIdList.size())
			throw new BusinessRuntimeException(ApplicationConstants.ERR_SERVICE_STEP_NOT_EXIST);
		
		List<CounterXServiceStep> existingSteps = counterXServiceStepRepository.findByCounterIdAndStepIdIn(counterId, stepsIdList);
		
		final Set<Long> stepIds1 = existingSteps.stream().map(CounterXServiceStep::getStepId).collect(Collectors.toSet());
		
		return stepsIdList.stream().filter(id -> !stepIds1.contains(id)).collect(Collectors.toList());
	}
	
	private void checkOperatorAccess(String userId, Counter counter) {
		if(!counter.getCurrentOperator().equals(userId))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_ACCESS_DENIED);
	}
	
	private User getOperator(String operatorId) {
		User operator = userService.getUser(operatorId);
		if(Objects.isNull(operator))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_OPERATOR_NOT_EXIST);
		return operator;
	}
	
	private void checkAccess(String userId, Branch branch) {
		if(!branch.getManagerId().equals(userId))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_ACCESS_DENIED);
	}
	
	private Branch getBranch(Long branchId) {
		Branch branch =  branchService.getBranch(branchId);
		if(Objects.isNull(branch))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		return branch;
	}
	
	private Counter getCounter(Long branchId, Integer counterNumber) {
		Counter counter = branchService.getBranch(branchId).getCounter(counterNumber);
		if(Objects.isNull(counter))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_COUNTER_NOT_EXIST);
		return counter;
	}
}

