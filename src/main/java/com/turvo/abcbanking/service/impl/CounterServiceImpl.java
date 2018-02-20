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
import com.turvo.abcbanking.model.User;
import com.turvo.abcbanking.repository.CounterRepository;
import com.turvo.abcbanking.repository.CounterXServiceStepRepository;
import com.turvo.abcbanking.repository.ServiceStepRepository;
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
public class CounterServiceImpl extends BaseServiceImpl implements CounterService{

	@Autowired
	BranchService branchService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	CounterRepository counterRepository;
	
	@Autowired
	ServiceStepRepository serviceStepRepository;
	
	@Autowired
	CounterXServiceStepRepository counterXServiceStepRepository;
	
	@Override
	public List<Counter> getAllBranchCounters(Long branchId) {
		return counterRepository.findByBranchId(getBranch(branchId).getId());
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
		
		return counterRepository.saveAndFlush(counter);
	}

	@Override
	@Transactional(readOnly = false)
	public Counter assignOperator(String assignerId, Long branchId, Integer counterNumber, String operatorId) {
		Branch branch =  getBranch(branchId);
		checkAccess(assignerId, branch);
		getOperator(operatorId);
		Counter counter = getCounter(branch, counterNumber);
		counter.setCurrentOperator(operatorId);
		counter.setLastModifiedBy(assignerId);
		
		return counterRepository.saveAndFlush(counter);
	}

	@Override
	public Counter getBranchCounter(Long branchId, Integer counterNumber) {
		Counter counter = counterRepository.findFirstByBranchIdAndNumber(branchId, counterNumber);
		if(!Objects.isNull(counter))
			counter.setSteps(serviceStepRepository.findByCounterId(counter.getId()));
		return counter;
	}

	@Override
	@Transactional(readOnly = false)
	public Counter assignSteps(String assignerId, Long branchId, Integer counterNumber, List<ServiceStep> steps) {
		Branch branch =  getBranch(branchId);
		checkAccess(assignerId, branch);
		Counter counter = getCounter(branch, counterNumber);
		List<Long> stepIds = getStepIdstoBeAdded(counter.getId(), steps);
		
		List<CounterXServiceStep> toBeAssignedSteps = new ArrayList<>();
		stepIds.forEach(step -> toBeAssignedSteps.add(new CounterXServiceStep(counter.getId(), step, assignerId)));
		
		counterXServiceStepRepository.save(toBeAssignedSteps);
		counterXServiceStepRepository.flush();
		
		counter.setSteps(serviceStepRepository.findByCounterId(counter.getId()));
		return counter;
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
	
	private Counter getCounter(Branch branch, Integer counterNumber) {
		Counter counter = counterRepository.findFirstByBranchIdAndNumber(branch.getId(), counterNumber);
		if(Objects.isNull(counter))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_COUNTER_NOT_EXIST);
		return counter;
	}
}
