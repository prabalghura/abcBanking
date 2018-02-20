package com.turvo.abcbanking.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.turvo.abcbanking.model.CounterXServiceStep;

/**
 * Standard repository class for Counter Service Step mapping operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
public interface CounterXServiceStepRepository extends BaseRepository<CounterXServiceStep, Long> {

	/**
	 * To get all existing step ids a counter currently serves
	 * so that it need not be added again
	 * 
	 * @param counterId
	 * @param stepIds
	 * @return
	 */
	public List<CounterXServiceStep> findByCounterIdAndStepIdIn(Long counterId, List<Long> stepIds);
}
