package com.turvo.abcbanking.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turvo.abcbanking.model.Service;
import com.turvo.abcbanking.model.ServiceStep;
import com.turvo.abcbanking.service.ServiceStepService;

/**
 * Controller class for Service related operations
 * 
 * @author Prabal Ghura
 *
 */
@RestController
@RequestMapping("/api")
public class ServiceController {
	
	@Autowired
	ServiceStepService serviceStepService;
	
	/**
	 * For getting all the services defined in the system
	 * 
	 * @return list of all services
	 */
	@RequestMapping("/services")
	public List<Service> getAllServices() {
		return serviceStepService.getAllServices();
	}
	
	/**
	 * For getting a specific service
	 * 
	 * @param serviceId
	 * @return service if existing
	 */
	@RequestMapping("/services/{id}")
	public Service getService(@PathVariable(value = "id") Long serviceId) {
		return serviceStepService.getService(serviceId);
	}
	
	/**
	 * For creating a new service
	 * 
	 * service is inactive until a workflow is defined in the system
	 * 
	 * @param creatorId
	 * @param service
	 * @return new service instance
	 */
	@PostMapping("/services")
	public Service createNewService(@RequestHeader("userId") String creatorId, @Valid @RequestBody Service service) {
	    return serviceStepService.createNewService(creatorId, service);
	}
	
	/**
	 * For getting all the service steps defined in the system
	 * 
	 * @return list of all service steps
	 */
	@RequestMapping("/serviceSteps")
	public List<ServiceStep> getAllServiceSteps() {
		return serviceStepService.getAllServiceSteps();
	}
	
	/**
	 * For getting a specific service step defined in the system
	 * 
	 * @param stepId
	 * @return service step if existing
	 */
	@RequestMapping("/serviceSteps/{id}")
	public ServiceStep getServiceStep(@PathVariable(value = "id") Long stepId) {
		return serviceStepService.getServiceStep(stepId);
	}
	
	/**
	 * For creating a new service step
	 * 
	 * @param creatorId
	 * @param step
	 * @return new step instance
	 */
	@PostMapping("/serviceSteps")
	public ServiceStep createNewServiceStep(@RequestHeader("userId") String creatorId, @Valid @RequestBody ServiceStep step) {
		return serviceStepService.createNewServiceStep(creatorId, step);
	}
	
	/**
	 * Assigns workflow to a service defined in the system
	 * Risky operation if changed when some tokens are active with same service, 
	 * those workflows will have to be served or manually cancelled.
	 * 
	 * To disable a service update its workflow with empty step list
	 * 
	 * @param creatorId
	 * @param serviceId
	 * @param step list
	 * @return updated service instance
	 */
	@PostMapping("/services/{id}/steps")
	public Service assignWorkFlowToService(@RequestHeader("userId") String creatorId, 
			@PathVariable(value = "id") Long serviceId, @Valid @RequestBody List<ServiceStep> steps) {
	    return serviceStepService.defineWorkFlowForService(creatorId, serviceId, steps);
	}
}
