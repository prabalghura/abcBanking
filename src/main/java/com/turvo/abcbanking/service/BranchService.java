package com.turvo.abcbanking.service;

import java.util.List;

import com.turvo.abcbanking.model.Branch;

/**
 * Service contract/interface for Branch operations
 * 
 * @author Prabal Ghura
 *
 */
public interface BranchService {

	/**
	 * Gets all the branches defined in the system.
	 * 
	 * @return list of branches
	 */
	public List<Branch> getAllBranches();
	
	/**
	 * Gets a specific branch by id.
	 * 
	 * @param branchId
	 * @return branch instance if exists null otherwise
	 */
	public Branch getBranch(Long branchId);
	
	/**
	 * Creates a new branch in the system
	 * 
	 * @param creatorId
	 * @param branch
	 * @return new branch instance
	 */
	public Branch createNewBranch(String creatorId, Branch branch);
	
	/**
	 * Assign a manager to a branch in the system
	 * 
	 * @param assignerId
	 * @param branchId
	 * @param managerId
	 * @return updated branch instance
	 */
	public Branch assignManager(String assignerId, Long branchId, String managerId);
}
