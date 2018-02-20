package com.turvo.abcbanking.service;

import java.util.List;

import com.turvo.abcbanking.model.Service;
import com.turvo.abcbanking.model.ServiceStep;

/**
 * Service contract/interface for Banking Service, Service step and corresponding mapping operations
 * 
 * @author Prabal Ghura
 *
 */
public interface ServiceStepService {

	/**
	 * To get list of all services defined in the system
	 * 
	 * @return list of services
	 */
	public List<Service> getAllServices();
	
	/**
	 * Gets a specific service by id.
	 * 
	 * @param serviceId
	 * @return service instance if exists null otherwise
	 */
	public Service getService(Long serviceId);
	
	/**
	 * Creates a new service in the system
	 * 
	 * @param creatorId
	 * @param service
	 * @return new service instance
	 */
	public Service createNewService(String creatorId, Service service);
	
	/**
	 * To get list of all service steps defined in the system
	 * 
	 * @return list of service steps
	 */
	public List<ServiceStep> getAllServiceSteps();
	
	/**
	 * Gets a specific service step by id.
	 * 
	 * @param stepId
	 * @return service step instance if exists null otherwise
	 */
	public ServiceStep getServiceStep(Long stepId);
	
	/**
	 * Creates a new service step in the system
	 * 
	 * @param creatorId
	 * @param step
	 * @return new service step instance
	 */
	public ServiceStep createNewServiceStep(String creatorId, ServiceStep step);
	
	/**
	 * Updates the service workflow in the system by overwriting existing one if any
	 * 
	 * @param creatorId
	 * @param serviceId
	 * @param steps
	 * @return updated Service instance
	 */
	public Service defineWorkFlowForService(String creatorId, Long serviceId, List<ServiceStep> steps);
}
