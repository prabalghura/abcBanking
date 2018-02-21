package com.turvo.abcbanking.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.model.Branch;
import com.turvo.abcbanking.model.CustomerType;
import com.turvo.abcbanking.model.Service;
import com.turvo.abcbanking.repository.BranchRepository;
import com.turvo.abcbanking.repository.ServiceRepository;
import com.turvo.abcbanking.service.BranchService;
import com.turvo.abcbanking.service.UserService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Service implementation for Branch operations
 * 
 * @author Prabal Ghura
 *
 */
@org.springframework.stereotype.Service("branchService")
public class BranchServiceImpl extends BaseServiceImpl implements BranchService {

	@Autowired
	BranchRepository branchRepository;
	
	@Autowired
	UserService userService;
	
	@Autowired
	ServiceRepository serviceRepository;
	
	@Override
	public List<Branch> getAllBranches() {
		return branchRepository.findAll();
	}

	@Override
	public Branch getBranch(Long id) {
		return branchRepository.findOne(id);
	}

	@Override
	@Transactional(readOnly = false)
	public Branch createNewBranch(String creatorId, Branch branch) {
		checkAccess(creatorId, ApplicationConstants.ROLE_ADD_NEW_BRANCH);
		if(Objects.isNull(userService.getUser(branch.getManagerId())))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_MANAGER_NOT_EXIST);
		branch.setLastModifiedBy(creatorId);
		return branchRepository.saveAndFlush(branch);
	}

	@Override
	@Transactional(readOnly = false)
	public Branch assignManager(String assignerId, Long branchId, String managerId) {
		checkAccess(assignerId, ApplicationConstants.ROLE_ADD_NEW_BRANCH);
		if(Objects.isNull(userService.getUser(managerId)))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_MANAGER_NOT_EXIST);
		Branch branch = getBranch(branchId);
		if(Objects.isNull(branch))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		branch.setManagerId(managerId);
		branch.setLastModifiedBy(assignerId);
		return branchRepository.saveAndFlush(branch);
	}
	
	public List<Service> getServices(Long branchId, CustomerType type) {
		return serviceRepository.getServicesForBranch(branchId, type);
	}
}
