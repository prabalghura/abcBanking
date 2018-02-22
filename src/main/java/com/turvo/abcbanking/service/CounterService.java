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
	 * Gets full fledged branch counters defined in the system.
	 * Used only during entire cache load
	 * 
	 * @param branchId
	 * @return list of counters
	 */
	public List<Counter> getBranchCountersFromDB(Long branchId);
	
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
	
	/**
	 * Services first token in the counter queue, reassigns to next counter if applicable
	 * 
	 * @param branchId
	 * @param counterNumber
	 * @param comments
	 */
	public void serviceFirstCounter(String executorId, Long branchId, Integer counterNumber, String comments);
}
