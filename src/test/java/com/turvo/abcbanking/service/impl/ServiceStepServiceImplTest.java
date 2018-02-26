package com.turvo.abcbanking.service.impl;

import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
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
	
	// These are configurable settings
	
	// stubbedServiceId and nonExistingServiceId should be mutually exclusive
	Long stubbedServiceId = 1L;
	Long nonExistingServiceId = 2L;
	
	// nonExistingServiceStepId and stubbedServiceStepId should be mutually exclusive
	Long nonExistingServiceStepId = 1L;
	Long stubbedServiceStepId = 2L;

	/**
	 * Stubbing all dependencies at one place
	 */
	@Before
	public final void stubDependencies() {
		ServiceStep step = new ServiceStep();
		step.setId(stubbedServiceStepId);
		List<ServiceStep> stepList = new ArrayList<>();
		stepList.add(step);
		
		Service service = new Service();
		service.setId(stubbedServiceId);
		List<Service> serviceList = new ArrayList<>();
		serviceList.add(service);
		
		Mockito.when(serviceRepository.findAll()).thenReturn(serviceList);
		Mockito.when(serviceRepository.findOne(stubbedServiceId)).thenReturn(service);
		Mockito.when(serviceStepRepository.findAll()).thenReturn(stepList);
		Mockito.when(serviceStepRepository.findOne(stubbedServiceId)).thenReturn(step);
		Mockito.when(serviceRepository.saveAndFlush(any(Service.class))).then(AdditionalAnswers.returnsFirstArg());
		Mockito.when(serviceStepRepository.saveAndFlush(any(ServiceStep.class))).then(AdditionalAnswers.returnsFirstArg());
		Mockito.when(roleService.checkAccessForUser("userIdWithAccess", ApplicationConstants.ROLE_DEFINE_SERVICE)).thenReturn(true);
		Mockito.when(serviceStepRepository.findByIdIn(Arrays.asList(stubbedServiceStepId))).thenReturn(stepList);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#getAllServices()}.
	 */
	@Test
	public final void testGetAllServices() {
		Assert.assertFalse("Service list should not be null", Objects.isNull(serviceStepService.getAllServices()));
		Assert.assertFalse("Service list should contain stubbed instance", Objects.isNull(serviceStepService.getAllServices().get(0)));
		Assert.assertTrue("Service instance should be same as stubbed instance", serviceStepService.getAllServices().get(0)
				.getId() == stubbedServiceId);
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#getService(java.lang.Long)}.
	 */
	@Test
	public final void testGetService() {
		Assert.assertFalse("Service should not be null", Objects.isNull(serviceStepService.getService(stubbedServiceId)));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#createNewService(java.lang.String, com.turvo.abcbanking.model.Service)}.
	 */
	@Test
	public final void testCreateNewServiceWithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		serviceStepService.createNewService("userIdWithoutAccess", new Service());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#createNewService(java.lang.String, com.turvo.abcbanking.model.Service)}.
	 */
	@Test
	public final void testCreateNewServiceNullServiceName() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_INVALID_SERVICE_NAME);
		serviceStepService.createNewService("userIdWithAccess", new Service());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#createNewService(java.lang.String, com.turvo.abcbanking.model.Service)}.
	 */
	@Test
	public final void testCreateNewServiceValid() {
		Service service = new Service();
		service.setName("service");
		service = serviceStepService.createNewService("userIdWithAccess", service);
		Assert.assertTrue("Created instance should be same as passed instance", service.getName().equalsIgnoreCase("service"));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#getAllServiceSteps()}.
	 */
	@Test
	public final void testGetAllServiceSteps() {
		Assert.assertFalse("Service Step list should not be null", Objects.isNull(serviceStepService.getAllServiceSteps()));
		Assert.assertFalse("Service Step list should contain stubbed instance", Objects.isNull(serviceStepService.getAllServiceSteps().get(0)));
		Assert.assertTrue("Service Step instance should be same as stubbed instance", serviceStepService.getAllServiceSteps().get(0)
				.getId() == stubbedServiceStepId);
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#getServiceStep(java.lang.Long)}.
	 */
	@Test
	public final void testGetServiceStep() {
		Assert.assertFalse("Service step should not be null", Objects.isNull(serviceStepService.getServiceStep(stubbedServiceId)));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#createNewServiceStep(java.lang.String, com.turvo.abcbanking.model.ServiceStep)}.
	 */
	@Test
	public final void testCreateNewServiceStepWithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		serviceStepService.createNewServiceStep("userIdWithoutAccess", new ServiceStep());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#createNewServiceStep(java.lang.String, com.turvo.abcbanking.model.ServiceStep)}.
	 */
	@Test
	public final void testCreateNewServiceStepNullStepName() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_INVALID_SERVICE_STEP_NAME);
		serviceStepService.createNewServiceStep("userIdWithAccess", new ServiceStep());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#createNewServiceStep(java.lang.String, com.turvo.abcbanking.model.ServiceStep)}.
	 */
	@Test
	public final void testCreateNewServiceStepValid() {
		ServiceStep step = new ServiceStep();
		step.setName("step");
		step = serviceStepService.createNewServiceStep("userIdWithAccess", step);
		Assert.assertTrue("Created instance should be same as passed instance", step.getName().equalsIgnoreCase("step"));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#defineWorkFlowForService(java.lang.String, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testDefineWorkFlowForServiceWithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		serviceStepService.defineWorkFlowForService("userIdWithoutAccess", stubbedServiceId, new ArrayList<>());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#defineWorkFlowForService(java.lang.String, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testDefineWorkFlowForServiceInvalidService() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_SERVICE_NOT_EXIST);
		serviceStepService.defineWorkFlowForService("userIdWithAccess", nonExistingServiceId, new ArrayList<>());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#defineWorkFlowForService(java.lang.String, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testDefineWorkFlowForServiceValidEmpty() {
		serviceStepService.defineWorkFlowForService("userIdWithAccess", stubbedServiceId, new ArrayList<>());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#defineWorkFlowForService(java.lang.String, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testDefineWorkFlowForServiceNullStep() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_INVALID_SERVICE_STEP_ID);
		serviceStepService.defineWorkFlowForService("userIdWithAccess", stubbedServiceId, Arrays.asList(new ServiceStep()));
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#defineWorkFlowForService(java.lang.String, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testDefineWorkFlowForServiceNonExistingStep() {
		ServiceStep step = new ServiceStep();
		step.setId(nonExistingServiceStepId);
		List<ServiceStep> stepList = Arrays.asList(step, step, step);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_SERVICE_STEP_NOT_EXIST);
		serviceStepService.defineWorkFlowForService("userIdWithAccess", stubbedServiceId, stepList);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.ServiceStepServiceImpl#defineWorkFlowForService(java.lang.String, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testDefineWorkFlowForServiceValidNonEmpty() {
		ServiceStep step = new ServiceStep();
		step.setId(stubbedServiceStepId);
		List<ServiceStep> stepList = Arrays.asList(step, step, step);
		serviceStepService.defineWorkFlowForService("userIdWithAccess", stubbedServiceId, stepList);
	}
}
