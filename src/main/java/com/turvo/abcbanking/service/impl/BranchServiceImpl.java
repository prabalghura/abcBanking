package com.turvo.abcbanking.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.model.Branch;
import com.turvo.abcbanking.repository.BranchRepository;
import com.turvo.abcbanking.service.BranchService;
import com.turvo.abcbanking.service.RoleService;
import com.turvo.abcbanking.service.UserService;

/**
 * Service implementation for Branch operations
 * 
 * @author Prabal Ghura
 *
 */
@Service("branchService")
public class BranchServiceImpl implements BranchService {

	@Autowired
	BranchRepository branchRepository;
	
	@Autowired
	RoleService roleService;
	
	@Autowired
	UserService userService;
	
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
		roleService.checkAccess(creatorId, "ADD_NEW_BRANCH");
		if(Objects.isNull(userService.getUser(branch.getManagerId())))
			throw new BusinessRuntimeException("Manager does not exist");
		branch.setLastModifiedBy(creatorId);
		return branchRepository.saveAndFlush(branch);
	}

	@Override
	@Transactional(readOnly = false)
	public Branch assignManager(String assignerId, Long branchId, String managerId) {
		roleService.checkAccess(assignerId, "ADD_NEW_BRANCH");
		if(Objects.isNull(userService.getUser(managerId)))
			throw new BusinessRuntimeException("Manager does not exist");
		Branch branch = getBranch(branchId);
		if(Objects.isNull(branch))
			throw new BusinessRuntimeException("Branch does not exist");
		branch.setManagerId(managerId);
		branch.setLastModifiedBy(assignerId);
		return branchRepository.saveAndFlush(branch);
	}
}
