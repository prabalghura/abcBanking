package com.turvo.abcbanking.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.model.Service;
import com.turvo.abcbanking.model.ServiceStep;
import com.turvo.abcbanking.model.ServiceXServiceStep;
import com.turvo.abcbanking.repository.ServiceRepository;
import com.turvo.abcbanking.repository.ServiceStepRepository;
import com.turvo.abcbanking.repository.ServiceXServiceStepRepository;
import com.turvo.abcbanking.service.ServiceStepService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Service implementation for Service operations
 * 
 * @author Prabal Ghura
 *
 */
@org.springframework.stereotype.Service("serviceStepService")
public class ServiceStepServiceImpl extends BaseServiceImpl implements ServiceStepService{

	@Autowired
	ServiceRepository serviceRepository;
	
	@Autowired
	ServiceStepRepository serviceStepRepository;
	
	@Autowired
	ServiceXServiceStepRepository serviceXServiceStepRepository;
	
	@Override
	public List<Service> getAllServices() {
		return serviceRepository.findAll();
	}

	@Override
	public Service getService(Long serviceId) {
		Service service = serviceRepository.findOne(serviceId);
		if(!Objects.isNull(service))
			service.setSteps(serviceStepRepository.findByServiceId(serviceId));
		return service;
	}

	@Override
	@Transactional(readOnly = false)
	public Service createNewService(String creatorId, Service service) {
		checkAccess(creatorId, ApplicationConstants.ROLE_DEFINE_SERVICE);
		if(Objects.isNull(service.getName()))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_INVALID_SERVICE_NAME);
		service.setCreatedBy(creatorId);
		return serviceRepository.saveAndFlush(service);
	}

	@Override
	public List<ServiceStep> getAllServiceSteps() {
		return serviceStepRepository.findAll();
	}

	@Override
	public ServiceStep getServiceStep(Long stepId) {
		return serviceStepRepository.findOne(stepId);
	}

	@Override
	@Transactional(readOnly = false)
	public ServiceStep createNewServiceStep(String creatorId, ServiceStep step) {
		checkAccess(creatorId, ApplicationConstants.ROLE_DEFINE_SERVICE);
		if(Objects.isNull(step.getName()))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_INVALID_SERVICE_STEP_NAME);
		step.setCreatedBy(creatorId);
		return serviceStepRepository.saveAndFlush(step);
	}

	@Override
	@Transactional(readOnly = false)
	public Service defineWorkFlowForService(String creatorId, Long serviceId, List<ServiceStep> steps) {
		checkAccess(creatorId, ApplicationConstants.ROLE_DEFINE_SERVICE);
		if(Objects.isNull(getService(serviceId)))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_SERVICE_NOT_EXIST);
		steps.forEach(step -> {
			if(Objects.isNull(step.getId()))
				throw new BusinessRuntimeException(ApplicationConstants.ERR_INVALID_SERVICE_STEP_ID);
		});
		Set<Long> stepIds = steps.stream().map(ServiceStep::getId).collect(Collectors.toSet());
		List<Long> stepsIdList = new ArrayList<>();
		stepIds.forEach(stepsIdList::add);
		
		List<ServiceStep> dbSteps = serviceStepRepository.findByIdIn(stepsIdList);
		
		if(dbSteps.size() != stepsIdList.size())
			throw new BusinessRuntimeException(ApplicationConstants.ERR_SERVICE_STEP_NOT_EXIST);
		
		List<ServiceXServiceStep> workflowList = new ArrayList<>();
		int order = 1;
		for(ServiceStep step: steps) {
			ServiceXServiceStep workflow = new ServiceXServiceStep();
			workflow.setCreatedBy(creatorId);
			workflow.setOrder(order++);
			workflow.setServiceId(serviceId);
			workflow.setStepId(step.getId());
			workflowList.add(workflow);
		}
		
		serviceXServiceStepRepository.deleteServiceWorkflow(serviceId);
		serviceXServiceStepRepository.save(workflowList);
		serviceXServiceStepRepository.flush();
		
		return getService(serviceId);
	}
}
