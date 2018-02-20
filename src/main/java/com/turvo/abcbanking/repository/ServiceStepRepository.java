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
	
	@Query(CustomQueries.WORKFLOW_FOR_SERVICE)
	public List<ServiceStep> findByServiceId(Long serviceId);
}
