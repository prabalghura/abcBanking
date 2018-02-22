package com.turvo.abcbanking.service;

import java.util.List;

import com.turvo.abcbanking.model.Branch;
import com.turvo.abcbanking.model.Counter;
import com.turvo.abcbanking.model.CustomerType;

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
	
	/**
	 * Gets the best counter in a branch serving a service step
	 * 
	 * @param branchId
	 * @param type
	 * @param stepId
	 * @return
	 */
	public Counter getBestCounter(Long branchId, CustomerType type, Long stepId);
	
	/**
	 * updates a branch in the cache, best used for day end operation for resetting token counter
	 * when all tokens are served
	 * 
	 * @param branchId
	 * @return
	 */
	public Branch updateBranch(String managerId, Long branchId);
	
	/**
	 * Update a counter within a branch in the cache (needed for other services only)
	 * Using services must ensure that passed counter instance have valid branch Id
	 * 
	 * @param counter
	 * @return updated counter instance
	 */
	public Counter updateCounter(Counter counter);
	
	/**
	 * To scrap entire branch cache and reload it from DB 
	 * used in case of change in service to service step mapping only
	 * 
	 */
	public void reloadEntireCache();
}
