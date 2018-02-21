package com.turvo.abcbanking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.turvo.abcbanking.model.ServiceStep;
import com.turvo.abcbanking.utils.CustomQueries;

/**
 * Standard repository class for Service Step operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
public interface ServiceStepRepository extends BaseRepository<ServiceStep, Long> {
	
	/**
	 * Find steps by id (in clause)
	 * 
	 * @param ids
	 * @return
	 */
	public List<ServiceStep> findByIdIn(List<Long> ids);
	
	/**
	 * To get all the service steps defined for a service in order
	 * 
	 * @param serviceId
	 * @return list of ordered steps
	 */
	@Query(CustomQueries.WORKFLOW_FOR_SERVICE)
	public List<ServiceStep> findByServiceId(Long serviceId);
	
	/**
	 * To get all the service steps currently served by a counter
	 * 
	 * @param counterId
	 * @return list of service steps
	 */
	@Query(CustomQueries.STEPS_FOR_COUNTER)
	public List<ServiceStep> findByCounterId(Long counterId);
}
