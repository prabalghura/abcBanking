package com.turvo.abcbanking.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.model.Branch;
import com.turvo.abcbanking.model.Counter;
import com.turvo.abcbanking.model.Customer;
import com.turvo.abcbanking.model.CustomerType;
import com.turvo.abcbanking.model.Service;
import com.turvo.abcbanking.model.ServiceStep;
import com.turvo.abcbanking.model.Token;
import com.turvo.abcbanking.model.TokenStatus;
import com.turvo.abcbanking.model.TokenWorkflow;
import com.turvo.abcbanking.model.TokenWorklowStatus;
import com.turvo.abcbanking.repository.TokenRepository;
import com.turvo.abcbanking.repository.TokenWorkflowRepository;
import com.turvo.abcbanking.service.BranchService;
import com.turvo.abcbanking.service.TokenService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Test class for Token service
 * 
 * @author Prabal Ghura
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TokenServiceImplTest {
	
	@Autowired
	TokenService tokenService;
	
	@MockBean
	BranchService branchService;
	
	@MockBean
	TokenRepository tokenRepository;
	
	@MockBean
	TokenWorkflowRepository tokenWorkflowRepository;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// These are configurable values
	Integer regularServiceSteps = 2;
	Integer premiumServiceSteps = 3;
	Long customerAccountNumber = 1234567890123456L;
	
	// stubbedBranchId and nonStubbedBranchId should be mutually exclusive
	Long stubbedBranchId = 1L;
	Long nonStubbedBranchId = 2L;
	
	// nonExistingServiceIds should be mutually exclusive with regularServiceIds and premiumServiceIds
	Long[] regularServiceIds = {1L, 4L};
	Long[] premiumServiceIds = {7L, 9L};
	Long[] nonExistingServiceIds = {2L, 3L};
	
	Long regularCounterId = 1L;
	Long premiumCounterId = 2L;
	Integer regularCounterNumber = 1;
	Integer premiumCounterNumber = 2;
	
	// regularTokenNumber and nonExistingTokenNumber should be mutually exclusive
	Integer regularTokenNumber = 1;
	Integer nonExistingTokenNumber = 2;
	
	/**
	 * To create service instances for mocking
	 * 
	 * @param stepCount
	 * @param serviceIds
	 * @return
	 */
	private List<Service> createServicesWithSteps(Integer stepCount, Long... serviceIds) {
		List<Service> services = new ArrayList<>();
		
		for(Long id: serviceIds) {
			Service service = new Service();
			service.setId(id);
			List<ServiceStep> steps = new ArrayList<>();
			for(int i=0; i<stepCount; i++) {
				ServiceStep step = new ServiceStep();
				step.setId(Long.parseLong(id.toString()+i));
				steps.add(step);
			}
			service.setSteps(steps);
			services.add(service);
		}
		
		return services;
	}
	
	/**
	 * Stubbing all dependencies at one place
	 */
	@Before
	public final void stubDependencies() {
		Branch branch = new Branch();
		branch.setRegularServices(createServicesWithSteps(regularServiceSteps, regularServiceIds));
		branch.setPremiumServices(createServicesWithSteps(premiumServiceSteps, premiumServiceIds));
		branch.setTokenNumber(0);
		branch.setManagerId("validManager");
		
		Token regularToken = new Token();
		regularToken.setAccountNumber(customerAccountNumber);
		regularToken.setNumber(regularTokenNumber);
		regularToken.setStatus(TokenStatus.PENDING);
		regularToken.setType(CustomerType.REGULAR);
		
		Counter counter = new Counter();
		counter.setServicingType(CustomerType.REGULAR);
		counter.setId(regularCounterId);
		counter.setNumber(regularCounterNumber);
		counter.setBranchId(stubbedBranchId);
		counter.setCurrentOperator("validOperatorRegular");
		counter.addToken(regularToken);
		branch.updateCounter(counter);
		
		counter = new Counter();
		counter.setServicingType(CustomerType.PREMIUM);
		counter.setId(premiumCounterId);
		counter.setNumber(premiumCounterNumber);
		counter.setBranchId(stubbedBranchId);
		branch.updateCounter(counter);
		
		Mockito.when(branchService.getBranch(stubbedBranchId)).thenReturn(branch);
		Mockito.when(tokenRepository.save(any(Token.class))).then(AdditionalAnswers.returnsFirstArg());
		Mockito.when(branchService.getBestCounter(eq(stubbedBranchId), eq(CustomerType.REGULAR), anyLong())).thenReturn(branch.getCounter(regularCounterNumber));
		Mockito.when(branchService.getBestCounter(eq(stubbedBranchId), eq(CustomerType.PREMIUM), anyLong())).thenReturn(branch.getCounter(premiumCounterNumber));
		Mockito.when(tokenWorkflowRepository.save(anyListOf(TokenWorkflow.class))).thenAnswer(new Answer<Object>() {
		    public Object answer(InvocationOnMock invocation) {
		        return invocation.getArguments()[0];
		    }
		});
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#createToken(com.turvo.abcbanking.model.Customer, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testCreateToken_ServiceMixedExistingNonExisting() {
		Customer customer = new Customer();
		customer.setAccountNumber(customerAccountNumber);
		customer.setType(CustomerType.PREMIUM);
		List<Service> services = new ArrayList<>();
		Service service;
		for(Long id: premiumServiceIds) {
			service = new Service();
			service.setId(id);
			services.add(service);
		}
		for(Long id: nonExistingServiceIds) {
			service = new Service();
			service.setId(id);
			services.add(service);
		}
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_INVALID_SERVICE);
		tokenService.createToken(customer, stubbedBranchId, services);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#createToken(com.turvo.abcbanking.model.Customer, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testCreateToken_ServiceMultipleNonExisting() {
		Customer customer = new Customer();
		customer.setAccountNumber(customerAccountNumber);
		customer.setType(CustomerType.PREMIUM);
		List<Service> services = new ArrayList<>();
		Service service;
		for(Long id: nonExistingServiceIds) {
			service = new Service();
			service.setId(id);
			services.add(service);
		}
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_INVALID_SERVICE);
		tokenService.createToken(customer, stubbedBranchId, services);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#createToken(com.turvo.abcbanking.model.Customer, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testCreateToken_ServiceSingleNonExisting() {
		Customer customer = new Customer();
		customer.setAccountNumber(customerAccountNumber);
		customer.setType(CustomerType.PREMIUM);
		List<Service> services = new ArrayList<>();
		Service service;
		service = new Service();
		service.setId(nonExistingServiceIds[0]);
		services.add(service);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_INVALID_SERVICE);
		tokenService.createToken(customer, stubbedBranchId, services);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#createToken(com.turvo.abcbanking.model.Customer, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testCreateToken_ServiceDuplicate() {
		Customer customer = new Customer();
		customer.setAccountNumber(customerAccountNumber);
		customer.setType(CustomerType.PREMIUM);
		List<Service> services = new ArrayList<>();
		Service service;
		service = new Service();
		service.setId(1L);
		services.add(service);
		services.add(service);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_TOKEN_DUPLICATE_SERVICE);
		tokenService.createToken(customer, stubbedBranchId, services);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#createToken(com.turvo.abcbanking.model.Customer, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testCreateToken_ServicePremiumMultipleValid() {
		Customer customer = new Customer();
		customer.setAccountNumber(customerAccountNumber);
		customer.setType(CustomerType.PREMIUM);
		List<Service> services = new ArrayList<>();
		Service service;
		for(Long id: premiumServiceIds) {
			service = new Service();
			service.setId(id);
			services.add(service);
		}
		Token token = tokenService.createToken(customer, stubbedBranchId, services);
		Assert.assertNotNull("Token must not be null", token);
		Assert.assertTrue("Token must be generated for the passed customer", token.getAccountNumber() == customer.getAccountNumber());
		Assert.assertTrue("Token must be generated as being pending", token.getStatus() == TokenStatus.PENDING);
		Assert.assertTrue("Token type must be same as customer type", token.getType() == customer.getType());
		Assert.assertNotNull("Token workflow must not be null", token.getSteps());
		Assert.assertFalse("Token Workflow must not be empty", token.getSteps().isEmpty());
		Assert.assertTrue("Token Workflow must be created according to service workflow", token.getSteps().size() == premiumServiceIds.length*premiumServiceSteps);
		Assert.assertTrue("Token's first step must be assigned", token.getSteps().get(0).getStatus() == TokenWorklowStatus.ASSIGNED);
		Assert.assertTrue("Token's workflow must be assigned to best Premium Counter", token.getSteps().get(0).getCounterId() == premiumCounterId);
		Assert.assertTrue("Token must be assigned to best Premium Counter", token.getCounterNumber() == premiumCounterNumber);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#createToken(com.turvo.abcbanking.model.Customer, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testCreateToken_ServicePremiumSingleValid() {
		Customer customer = new Customer();
		customer.setAccountNumber(customerAccountNumber);
		customer.setType(CustomerType.PREMIUM);
		List<Service> services = new ArrayList<>();
		Service service = new Service();
		service.setId(premiumServiceIds[0]);
		services.add(service);
		Token token = tokenService.createToken(customer, stubbedBranchId, services);
		Assert.assertNotNull("Token must not be null", token);
		Assert.assertTrue("Token must be generated for the passed customer", token.getAccountNumber() == customer.getAccountNumber());
		Assert.assertTrue("Token must be generated as being pending", token.getStatus() == TokenStatus.PENDING);
		Assert.assertTrue("Token type must be same as customer type", token.getType() == customer.getType());
		Assert.assertNotNull("Token workflow must not be null", token.getSteps());
		Assert.assertFalse("Token Workflow must not be empty", token.getSteps().isEmpty());
		Assert.assertTrue("Token Workflow must be created according to service workflow", token.getSteps().size() == premiumServiceSteps);
		Assert.assertTrue("Token's first step must be assigned", token.getSteps().get(0).getStatus() == TokenWorklowStatus.ASSIGNED);
		Assert.assertTrue("Token's workflow must be assigned to best Premium Counter", token.getSteps().get(0).getCounterId() == premiumCounterId);
		Assert.assertTrue("Token must be assigned to best Premium Counter", token.getCounterNumber() == premiumCounterNumber);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#createToken(com.turvo.abcbanking.model.Customer, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testCreateToken_InvalidBranchId() {
		Customer customer = new Customer();
		customer.setAccountNumber(customerAccountNumber);
		customer.setType(CustomerType.REGULAR);
		List<Service> services = new ArrayList<>();
		Service service;
		for(Long id: regularServiceIds) {
			service = new Service();
			service.setId(id);
			services.add(service);
		}
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		tokenService.createToken(customer, nonStubbedBranchId, services);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#createToken(com.turvo.abcbanking.model.Customer, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testCreateToken_ServiceRegularMultipleValid() {
		Customer customer = new Customer();
		customer.setAccountNumber(customerAccountNumber);
		customer.setType(CustomerType.REGULAR);
		List<Service> services = new ArrayList<>();
		Service service;
		for(Long id: regularServiceIds) {
			service = new Service();
			service.setId(id);
			services.add(service);
		}
		Token token = tokenService.createToken(customer, stubbedBranchId, services);
		Assert.assertNotNull("Token must not be null", token);
		Assert.assertTrue("Token must be generated for the passed customer", token.getAccountNumber() == customer.getAccountNumber());
		Assert.assertTrue("Token must be generated as being pending", token.getStatus() == TokenStatus.PENDING);
		Assert.assertTrue("Token type must be same as customer type", token.getType() == customer.getType());
		Assert.assertNotNull("Token workflow must not be null", token.getSteps());
		Assert.assertFalse("Token Workflow must not be empty", token.getSteps().isEmpty());
		Assert.assertTrue("Token Workflow must be created according to both service workflow", token.getSteps().size() == regularServiceIds.length*regularServiceSteps);
		Assert.assertTrue("Token's first step must be assigned", token.getSteps().get(0).getStatus() == TokenWorklowStatus.ASSIGNED);
		Assert.assertTrue("Token's workflow must be assigned to best Regular Counter", token.getSteps().get(0).getCounterId() == regularCounterId);
		Assert.assertTrue("Token must be assigned to best Regular Counter", token.getCounterNumber() == regularCounterNumber);
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#createToken(com.turvo.abcbanking.model.Customer, java.lang.Long, java.util.List)}.
	 */
	@Test
	public final void testCreateToken_ServiceRegularSingleValid() {
		Customer customer = new Customer();
		customer.setAccountNumber(customerAccountNumber);
		customer.setType(CustomerType.REGULAR);
		List<Service> services = new ArrayList<>();
		Service service = new Service();
		service.setId(regularServiceIds[0]);
		services.add(service);
		Token token = tokenService.createToken(customer, stubbedBranchId, services);
		Assert.assertNotNull("Token must not be null", token);
		Assert.assertTrue("Token must be generated for the passed customer", token.getAccountNumber() == customer.getAccountNumber());
		Assert.assertTrue("Token must be generated as being pending", token.getStatus() == TokenStatus.PENDING);
		Assert.assertTrue("Token type must be same as customer type", token.getType() == customer.getType());
		Assert.assertNotNull("Token workflow must not be null", token.getSteps());
		Assert.assertFalse("Token Workflow must not be empty", token.getSteps().isEmpty());
		Assert.assertTrue("Token Workflow must be created according to service workflow", token.getSteps().size() == regularServiceSteps);
		Assert.assertTrue("Token's first step must be assigned", token.getSteps().get(0).getStatus() == TokenWorklowStatus.ASSIGNED);
		Assert.assertTrue("Token's workflow must be assigned to best Regular Counter", token.getSteps().get(0).getCounterId() == regularCounterId);
		Assert.assertTrue("Token must be assigned to best Regular Counter", token.getCounterNumber() == regularCounterNumber);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#markTokenAsCompleted(java.lang.String, java.lang.Long, java.lang.Integer)}.
	 */
	@Test
	public final void testMarkTokenAsCompleted_WithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		tokenService.markTokenAsCompleted("invalidUser", stubbedBranchId, regularTokenNumber);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#markTokenAsCompleted(java.lang.String, java.lang.Long, java.lang.Integer)}.
	 */
	@Test
	public final void testMarkTokenAsCompleted_NonExistingToken() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_TOKEN_NOT_EXIST);
		tokenService.markTokenAsCompleted("validManager", stubbedBranchId, nonExistingTokenNumber);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#markTokenAsCompleted(java.lang.String, java.lang.Long, java.lang.Integer)}.
	 */
	@Test
	public final void testMarkTokenAsCompleted_InvalidBranch() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		tokenService.markTokenAsCompleted("validManager", nonStubbedBranchId, regularTokenNumber);
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#markTokenAsCompleted(java.lang.String, java.lang.Long, java.lang.Integer)}.
	 */
	@Test
	public final void testMarkTokenAsCompleted_ByOperatorValid() {
		Assert.assertNotNull("Counter should have token originally", branchService.getBranch(stubbedBranchId).getCounter(regularCounterNumber).hasToken(regularTokenNumber));
		tokenService.markTokenAsCompleted("validOperatorRegular", stubbedBranchId, regularTokenNumber);
		Assert.assertNull("Counter should have been removed from counter", branchService.getBranch(stubbedBranchId).getCounter(regularCounterNumber).hasToken(regularTokenNumber));
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#markTokenAsCompleted(java.lang.String, java.lang.Long, java.lang.Integer)}.
	 */
	@Test
	public final void testMarkTokenAsCompleted_ByManagerValid() {
		Assert.assertNotNull("Counter should have token originally", branchService.getBranch(stubbedBranchId).getCounter(regularCounterNumber).hasToken(regularTokenNumber));
		tokenService.markTokenAsCompleted("validManager", stubbedBranchId, regularTokenNumber);
		Assert.assertNull("Counter should have been removed from counter", branchService.getBranch(stubbedBranchId).getCounter(regularCounterNumber).hasToken(regularTokenNumber));
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#markTokenAsCancelled(java.lang.String, java.lang.Long, java.lang.Integer)}.
	 */
	@Test
	public final void testMarkTokenAsCancelled_WithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		tokenService.markTokenAsCancelled("invalidUser", stubbedBranchId, regularTokenNumber);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#markTokenAsCancelled(java.lang.String, java.lang.Long, java.lang.Integer)}.
	 */
	@Test
	public final void testMarkTokenAsCancelled_NonExistingToken() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_TOKEN_NOT_EXIST);
		tokenService.markTokenAsCancelled("validOperatorRegular", stubbedBranchId, nonExistingTokenNumber);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#markTokenAsCancelled(java.lang.String, java.lang.Long, java.lang.Integer)}.
	 */
	@Test
	public final void testMarkTokenAsCancelled_InvalidBranch() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		tokenService.markTokenAsCancelled("validOperatorRegular", nonStubbedBranchId, regularTokenNumber);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#markTokenAsCancelled(java.lang.String, java.lang.Long, java.lang.Integer)}.
	 */
	@Test
	public final void testMarkTokenAsCancelled_ByOperatorValid() {
		Assert.assertNotNull("Counter should have token originally", branchService.getBranch(stubbedBranchId).getCounter(regularCounterNumber).hasToken(regularTokenNumber));
		tokenService.markTokenAsCancelled("validOperatorRegular", stubbedBranchId, regularTokenNumber);
		Assert.assertNull("Counter should have been removed from counter", branchService.getBranch(stubbedBranchId).getCounter(regularCounterNumber).hasToken(regularTokenNumber));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.TokenServiceImpl#markTokenAsCancelled(java.lang.String, java.lang.Long, java.lang.Integer)}.
	 */
	@Test
	public final void testMarkTokenAsCancelled_ByManagerValid() {
		Assert.assertNotNull("Counter should have token originally", branchService.getBranch(stubbedBranchId).getCounter(regularCounterNumber).hasToken(regularTokenNumber));
		tokenService.markTokenAsCancelled("validManager", stubbedBranchId, regularTokenNumber);
		Assert.assertNull("Counter should have been removed from counter", branchService.getBranch(stubbedBranchId).getCounter(regularCounterNumber).hasToken(regularTokenNumber));
	}
}
