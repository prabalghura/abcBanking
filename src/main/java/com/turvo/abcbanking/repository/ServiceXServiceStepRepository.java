package com.turvo.abcbanking.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.turvo.abcbanking.model.ServiceXServiceStep;

/**
 * Standard repository class for Service and Service Step relationship operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
public interface ServiceXServiceStepRepository extends BaseRepository<ServiceXServiceStep, Long> {
	
	/**
	 * Delete existing workflow of a service defined in the system
	 * 
	 * @param serviceId
	 */
	@Modifying
    @Query("delete from ServiceXServiceStep sxs where sxs.serviceId = ?1")
    void deleteServiceWorkflow(Long serviceId);
}
