package com.turvo.abcbanking.service;

import java.util.List;

import com.turvo.abcbanking.model.Counter;
import com.turvo.abcbanking.model.ServiceStep;

/**
 * Service contract/interface for Counter operations
 * 
 * @author Prabal Ghura
 *
 */
public interface CounterService {

	/**
	 * Gets all the branch counters defined in the system.
	 * 
	 * @param branchId
	 * @return list of counters
	 */
	public List<Counter> getAllBranchCounters(Long branchId);
	
	/**
	 * Creates a new counter in the branch specified
	 * 
	 * @param creatorId
	 * @param branchId
	 * @param counter
	 * @return new counter instance
	 */
	public Counter createNewCounter(String creatorId, Long branchId, Counter counter);
	
	/**
	 * Assign an operator to a counter in the system
	 * 
	 * @param assignerId
	 * @param branchId
	 * @param counterNumber
	 * @param operatorId
	 * @return updated counter instance
	 */
	public Counter assignOperator(String assignerId, Long branchId, Integer counterNumber, String operatorId);
	
	/**
	 * Get a counter with all service steps it currently serves.
	 * 
	 * @param branchId
	 * @param counterNumber
	 * @return counter instance if exists 
	 */
	public Counter getBranchCounter(Long branchId, Integer counterNumber);
	
	/**
	 * Assigns a counter service steps it can service (exclusive addition)
	 * Existing steps are not modified, if existing steps are added they are simply ignored
	 * 
	 * @param assignerId
	 * @param branchId
	 * @param counterNumber
	 * @param list of steps
	 * @return
	 */
	public Counter assignSteps(String assignerId, Long branchId, Integer counterNumber, List<ServiceStep> steps);
}
