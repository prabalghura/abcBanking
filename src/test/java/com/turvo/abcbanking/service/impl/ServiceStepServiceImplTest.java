/**
 * 
 */
package com.turvo.abcbanking.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.model.Service;
import com.turvo.abcbanking.model.ServiceStep;
import com.turvo.abcbanking.repository.ServiceRepository;
import com.turvo.abcbanking.repository.ServiceStepRepository;
import com.turvo.abcbanking.repository.ServiceXServiceStepRepository;
import com.turvo.abcbanking.service.RoleService;
import com.turvo.abcbanking.service.ServiceStepService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Test class for Service Step service
 * 
 * @author Prabal Ghura
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceStepServiceImplTest {
	
	@Autowired
	ServiceStepService serviceStepService;
	
	@MockBean
	ServiceRepository serviceRepository;
	
	@MockBean
	ServiceStepRepository serviceStepRepository;
	
	@MockBean
	ServiceXServiceStepRepository serviceXServiceStepRepository;
	
	@MockBean
	RoleService roleService;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#getAllServices()}.
	 */
	@Test
	public final void testGetAllServices() {
		Mockito.when(serviceRepository.findAll()).thenReturn(new ArrayList<>());
		Assert.assertFalse("Service list should not be null", Objects.isNull(serviceStepService.getAllServices()));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#getService(java.lang.Long)}.
	 */
	@Test
	public final void testGetService() {
		Mockito.when(serviceRepository.findOne(1L)).thenReturn(new Service());
		Assert.assertFalse("Service should not be null", Objects.isNull(serviceStepService.getService(1L)));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#createNewService(java.lang.String, com.turvo.abcbanking.model.Service)}.
	 */
	@Test
	public final void testCreateNewServiceWithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		serviceStepService.createNewService("userId", new Service());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#createNewService(java.lang.String, com.turvo.abcbanking.model.Service)}.
	 */
	@Test
	public final void testCreateNewServiceNullServiceName() {
		Mockito.when(roleService.checkAccessForUser("userId", ApplicationConstants.ROLE_DEFINE_SERVICE)).thenReturn(true);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_INVALID_SERVICE_NAME);
		serviceStepService.createNewService("userId", new Service());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#createNewService(java.lang.String, com.turvo.abcbanking.model.Service)}.
	 */
	@Test
	public final void testCreateNewServiceValid() {
		Mockito.when(roleService.checkAccessForUser("userId", ApplicationConstants.ROLE_DEFINE_SERVICE)).thenReturn(true);
		Service service = new Service();
		service.setName("service");
		serviceStepService.createNewService("userId", service);
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#getAllServiceSteps()}.
	 */
	@Test
	public final void testGetAllServiceSteps() {
		Mockito.when(serviceStepRepository.findAll()).thenReturn(new ArrayList<>());
		Assert.assertFalse("Service Step list should not be null", Objects.isNull(serviceStepService.getAllServiceSteps()));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#getServiceStep(java.lang.Long)}.
	 */
	@Test
	public final void testGetServiceStep() {
		Mockito.when(serviceStepRepository.findOne(1L)).thenReturn(new ServiceStep());
		Assert.assertFalse("Service step should not be null", Objects.isNull(serviceStepService.getServiceStep(1L)));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#createNewServiceStep(java.lang.String, com.turvo.abcbanking.model.ServiceStep)}.
	 */
	@Test
	public final void testCreateNewServiceStepWithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		serviceStepService.createNewServiceStep("userId", new ServiceStep());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#createNewServiceStep(java.lang.String, com.turvo.abcbanking.model.ServiceStep)}.
	 */
	@Test
	public final void testCreateNewServiceStepNullStepName() {
		Mockito.when(roleService.checkAccessForUser("userId", ApplicationConstants.ROLE_DEFINE_SERVICE)).thenReturn(true);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_INVALID_SERVICE_STEP_NAME);
		serviceStepService.createNewServiceStep("userId", new ServiceStep());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#createNewServiceStep(java.lang.String, com.turvo.abcbanking.model.ServiceStep)}.
	 */
	@Test
	public final void testCreateNewServiceStepValid() {
		Mockito.when(roleService.checkAccessForUser("userId", ApplicationConstants.ROLE_DEFINE_SERVICE)).thenReturn(true);
		ServiceStep step = new ServiceStep();
		step.setName("step");
		serviceStepService.createNewServiceStep("userId", step);
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#defineWorkFlowForService(java.lang.String, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testDefineWorkFlowForServiceWithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		serviceStepService.defineWorkFlowForService("userId", 1L, new ArrayList<>());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#defineWorkFlowForService(java.lang.String, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testDefineWorkFlowForServiceInvalidService() {
		Mockito.when(roleService.checkAccessForUser("userId", ApplicationConstants.ROLE_DEFINE_SERVICE)).thenReturn(true);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_SERVICE_NOT_EXIST);
		serviceStepService.defineWorkFlowForService("userId", 1L, new ArrayList<>());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#defineWorkFlowForService(java.lang.String, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testDefineWorkFlowForServiceValidEmpty() {
		Mockito.when(roleService.checkAccessForUser("userId", ApplicationConstants.ROLE_DEFINE_SERVICE)).thenReturn(true);
		Mockito.when(serviceRepository.findOne(1L)).thenReturn(new Service());
		serviceStepService.defineWorkFlowForService("userId", 1L, new ArrayList<>());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#defineWorkFlowForService(java.lang.String, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testDefineWorkFlowForServiceNullStep() {
		Mockito.when(roleService.checkAccessForUser("userId", ApplicationConstants.ROLE_DEFINE_SERVICE)).thenReturn(true);
		Mockito.when(serviceRepository.findOne(1L)).thenReturn(new Service());
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_INVALID_SERVICE_STEP_ID);
		serviceStepService.defineWorkFlowForService("userId", 1L, Arrays.asList(new ServiceStep()));
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#defineWorkFlowForService(java.lang.String, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testDefineWorkFlowForServiceNonExistingStep() {
		Mockito.when(roleService.checkAccessForUser("userId", ApplicationConstants.ROLE_DEFINE_SERVICE)).thenReturn(true);
		Mockito.when(serviceRepository.findOne(1L)).thenReturn(new Service());
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_SERVICE_STEP_NOT_EXIST);
		ServiceStep step = new ServiceStep();
		step.setId(1L);
		serviceStepService.defineWorkFlowForService("userId", 1L, Arrays.asList(step));
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#defineWorkFlowForService(java.lang.String, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testDefineWorkFlowForServiceValidNonEmpty() {
		ServiceStep step = new ServiceStep();
		step.setId(1L);
		List<ServiceStep> stepList = Arrays.asList(step, step, step);
		List<Long> stepsIdList = Arrays.asList(1L);
		Mockito.when(roleService.checkAccessForUser("userId", ApplicationConstants.ROLE_DEFINE_SERVICE)).thenReturn(true);
		Mockito.when(serviceRepository.findOne(1L)).thenReturn(new Service());
		Mockito.when(serviceStepRepository.findByIdIn(stepsIdList)).thenReturn(Arrays.asList(step));
		serviceStepService.defineWorkFlowForService("userId", 1L, stepList);
	}
}
