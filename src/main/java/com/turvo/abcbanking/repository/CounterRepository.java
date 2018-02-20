package com.turvo.abcbanking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.turvo.abcbanking.model.Counter;
import com.turvo.abcbanking.utils.CustomQueries;

/**
 * Standard repository class for Counter operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
public interface CounterRepository extends BaseRepository<Counter, Long>{
	
	/**
	 * For finding list of counters in a branch
	 * 
	 * @param branchId
	 * @return list of counters branch currently have
	 */
	public List<Counter> findByBranchId(Long branchId);
	
	/**
	 * For finding max counter number in a branch
	 * 
	 * @param branchId
	 * @return max counter number
	 */
	@Query(CustomQueries.COUNTER_MAX_NUMBER)
	Integer getMaxCounterNumber(Long branchId);
	
	/**
	 * For finding counter by branchId and counter display number
	 * 
	 * @param branchId
	 * @param counterNumber
	 * @return counter if exists
	 */
	public Counter findFirstByBranchIdAndNumber(Long branchId, Integer number);
}
