package com.turvo.abcbanking.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.model.Branch;
import com.turvo.abcbanking.model.Counter;
import com.turvo.abcbanking.repository.CounterRepository;
import com.turvo.abcbanking.service.BranchService;
import com.turvo.abcbanking.service.CounterService;
import com.turvo.abcbanking.service.RoleService;
import com.turvo.abcbanking.service.UserService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Service implementation for Counter operations
 * 
 * @author Prabal Ghura
 *
 */
@Service("counterService")
public class CounterServiceImpl implements CounterService{

	@Autowired
	BranchService branchService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	RoleService roleService;
	
	@Autowired
	CounterRepository counterRepository;
	
	@Override
	public List<Counter> getAllBranchCounters(Long branchId) {
		if(Objects.isNull(branchService.getBranch(branchId)))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		return counterRepository.findByBranchId(branchId);
	}

	@Override
	@Transactional(readOnly = false)
	public Counter createNewCounter(String creatorId, Long branchId, Counter counter) {
		Branch branch =  branchService.getBranch(branchId);
		if(Objects.isNull(branch))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		if(!branch.getManagerId().equals(creatorId))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_ACCESS_DENIED);
		if(Objects.isNull(userService.getUser(counter.getCurrentOperator())))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_OPERATOR_NOT_EXIST);
		counter.setBranchId(branchId);
		counter.setNumber(counterRepository.getMaxCounterNumber(branchId) + 1);
		counter.setLastModifiedBy(creatorId);
		
		return counterRepository.saveAndFlush(counter);
	}

	@Override
	@Transactional(readOnly = false)
	public Counter assignOperator(String assignerId, Long branchId, Integer counterNumber, String operatorId) {
		Branch branch =  branchService.getBranch(branchId);
		if(Objects.isNull(branch))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		if(!branch.getManagerId().equals(assignerId))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_ACCESS_DENIED);
		if(Objects.isNull(userService.getUser(operatorId)))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_OPERATOR_NOT_EXIST);
		Counter counter = counterRepository.findFirstByBranchIdAndNumber(branchId, counterNumber);
		if(Objects.isNull(counter))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_COUNTER_NOT_EXIST);
		counter.setCurrentOperator(operatorId);
		counter.setLastModifiedBy(assignerId);
		
		return counterRepository.saveAndFlush(counter);
	}
}
