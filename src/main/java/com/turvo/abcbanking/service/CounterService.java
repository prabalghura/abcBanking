package com.turvo.abcbanking.service;

import java.util.List;

import com.turvo.abcbanking.model.Counter;

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
}
