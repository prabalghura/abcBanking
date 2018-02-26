package com.turvo.abcbanking.service.impl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.turvo.abcbanking.model.CustomerType;
import com.turvo.abcbanking.model.ServiceStep;
import com.turvo.abcbanking.model.Token;
import com.turvo.abcbanking.model.TokenStatus;
import com.turvo.abcbanking.model.User;
import com.turvo.abcbanking.repository.CounterRepository;
import com.turvo.abcbanking.repository.CounterXServiceStepRepository;
import com.turvo.abcbanking.repository.ServiceStepRepository;
import com.turvo.abcbanking.repository.TokenRepository;
import com.turvo.abcbanking.repository.TokenWorkflowRepository;
import com.turvo.abcbanking.service.BranchService;
import com.turvo.abcbanking.service.CounterService;
import com.turvo.abcbanking.service.UserService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Test class for Counter service
 * 
 * @author Prabal Ghura
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CounterServiceImplTest {
	
	@Autowired
	CounterService counterService;
	
	@MockBean
	BranchService branchService;
	
	@MockBean
	UserService userService;
	
	@MockBean
	CounterRepository counterRepository;
	
	@MockBean
	TokenRepository tokenRepository;
	
	@MockBean
	TokenWorkflowRepository tokenWorkflowRepository;
	
	@MockBean
	ServiceStepRepository serviceStepRepository;
	
	@MockBean
	CounterXServiceStepRepository counterXServiceStepRepository;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// These are configurable settings
	
	// stubbedBranchId & nonExistingBranchId should be mutually exclusive
	Long stubbedBranchId = 1L;
	Long nonExistingBranchId = 2L;
	
	Long stubbedCounterId = 1L;
	Long stubbedTokenId = 1L;
	
	// unauthorizedUserId & stubbedBranchManagerId should be mutually exclusive
	String stubbedBranchManagerId = "validBranchManager";
	String unauthorizedManagerId = "unauthorizedUserId";
	
	// stubbedOperatorId, stubbedNewOperatorId & invalidOperatorId should be mutually exclusive
	String stubbedOperatorId = "validOperator";
	String stubbedNewOperatorId = "validOperator1";
	String invalidOperatorId = "unauthorizedUserId";
	
	// stubbedCounterNumber & nonExistingCounterNumber should be mutually exclusive
	Integer stubbedCounterNumber = 1;
	Integer nonExistingCounterNumber = 2;
	
	// stubbedStepIds & nonExistingStepIds should be mutually exclusive
	List<Long> stubbedStepIds = Arrays.asList(1L, 4L);
	List<Long> nonExistingStepIds = Arrays.asList(7L, 8L);
	
	Integer stubbedTokenNumber = 1;
	CustomerType stubbedCustomerType = CustomerType.REGULAR;
	
	private List<ServiceStep> getServiceStepsFromIds(List<Long> ids) {
		List<ServiceStep> stepList = new ArrayList<>();
		for(Long id: ids) {
			stepList.add(getServiceStepsFromId(id));
		}
		return stepList;
	}
	
	private ServiceStep getServiceStepsFromId(Long id) {
		ServiceStep step = new ServiceStep();
		step.setId(id);
		return step;
	}
	
	private List<ServiceStep> getStubbedServiceStepsFromIds(List<Long> ids) {
		List<ServiceStep> stepList = new ArrayList<>();
		for(Long id: ids) {
			if(stubbedStepIds.contains(id))
				stepList.add(getServiceStepsFromId(id));
		}
		return stepList;
	}
	
	/**
	 * Stubbing all dependencies at one place
	 */
	@Before
	public final void stubDependencies() {
		Token token = new Token();
		token.setId(stubbedTokenId);
		token.setNumber(stubbedTokenNumber);
		token.setStatus(TokenStatus.PENDING);
		token.setType(stubbedCustomerType);
		
		Counter counter = new Counter();
		counter.setBranchId(stubbedBranchId);
		counter.setCurrentOperator(stubbedOperatorId);
		counter.setId(stubbedCounterId);
		counter.setNumber(stubbedCounterNumber);
		counter.setServicingType(stubbedCustomerType);
		counter.addToken(token);
		
		Branch branch = new Branch();
		branch.setId(stubbedBranchId);
		branch.setManagerId(stubbedBranchManagerId);
		branch.updateCounter(counter);
		
		List<ServiceStep> stepList = new ArrayList<>();
		ServiceStep step;
		for(Long id: stubbedStepIds) {
			step = new ServiceStep();
			step.setId(id);
			stepList.add(step);
		}
		
		Mockito.when(counterRepository.findByBranchId(stubbedBranchId)).thenReturn(Arrays.asList(counter));
		Mockito.when(tokenRepository.getTokensForCounter(stubbedCounterId)).thenReturn(Arrays.asList(token));
		Mockito.when(branchService.getBranch(stubbedBranchId)).thenReturn(branch);
		Mockito.when(userService.getUser(anyString())).thenReturn(null);
		Mockito.when(userService.getUser(stubbedOperatorId)).thenReturn(new User());
		Mockito.when(userService.getUser(stubbedBranchManagerId)).thenReturn(new User());
		Mockito.when(userService.getUser(stubbedNewOperatorId)).thenReturn(new User());
		Mockito.when(counterRepository.getMaxCounterNumber(stubbedBranchId)).thenReturn(stubbedCounterNumber);
		Mockito.when(counterRepository.saveAndFlush(any(Counter.class))).then(AdditionalAnswers.returnsFirstArg());
		Mockito.when(serviceStepRepository.findByIdIn(anyListOf(Long.class))).thenAnswer(new Answer<List<ServiceStep>>() {
			@Override
			public List<ServiceStep> answer(InvocationOnMock invocation) throws Throwable {
				@SuppressWarnings("unchecked")
				List<Long> ids = invocation.getArgumentAt(0, List.class);
				return getStubbedServiceStepsFromIds(ids);
			}
		});
		Mockito.when(branchService.updateBranch(stubbedBranchManagerId, stubbedBranchId)).thenReturn(branch);
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#getBranchCountersFromDB(java.lang.Long)}.
	 */
	@Test
	public final void testGetBranchCountersFromDB() {
		List<Counter> counters = counterService.getBranchCountersFromDB(stubbedBranchId);
		Assert.assertNotNull("Counter list should not be null", counters);
		Assert.assertFalse("Counter list should not be empty", counters.isEmpty());
		Assert.assertFalse("First Counter should have stubbed token", counters.get(0).getTokens().isEmpty());
		Token token = counters.get(0).getTokens().poll();
		Assert.assertNotNull("Stubbed token should be retrieved", token);
		Assert.assertTrue("Retrived token should be equal to stubbed token", token.getId() == stubbedTokenId);
		Assert.assertTrue("Retrived token should be equal to stubbed token", token.getNumber() == stubbedTokenNumber);
		Assert.assertTrue("Retrived token should be equal to stubbed token", token.getType() == stubbedCustomerType);
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#createNewCounter(java.lang.String, java.lang.Long, com.turvo.abcbanking.model.Counter)}.
	 */
	@Test
	public final void testCreateNewCounter_Valid() {
		Counter counter = new Counter();
		counter.setCurrentOperator(stubbedOperatorId);
		counter.setServicingType(stubbedCustomerType);
		counter = counterService.createNewCounter(stubbedBranchManagerId, stubbedBranchId, counter);
		Assert.assertNotNull("Created counter should not be null", counter);
		Assert.assertTrue("Created counter should have provided branch Id", counter.getBranchId() == stubbedBranchId);
		Assert.assertTrue("Created counter should have branch generated number", counter.getNumber() == stubbedCounterNumber + 1);
		Assert.assertTrue("Created counter should have track of its creation", counter.getLastModifiedBy() == stubbedBranchManagerId);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#createNewCounter(java.lang.String, java.lang.Long, com.turvo.abcbanking.model.Counter)}.
	 */
	@Test
	public final void testCreateNewCounter_InvalidBranchId() {
		Counter counter = new Counter();
		counter.setCurrentOperator(stubbedOperatorId);
		counter.setServicingType(stubbedCustomerType);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		counter = counterService.createNewCounter(stubbedBranchManagerId, nonExistingBranchId, counter);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#createNewCounter(java.lang.String, java.lang.Long, com.turvo.abcbanking.model.Counter)}.
	 */
	@Test
	public final void testCreateNewCounter_WithoutAccess() {
		Counter counter = new Counter();
		counter.setCurrentOperator(stubbedOperatorId);
		counter.setServicingType(stubbedCustomerType);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		counter = counterService.createNewCounter(unauthorizedManagerId, stubbedBranchId, counter);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#createNewCounter(java.lang.String, java.lang.Long, com.turvo.abcbanking.model.Counter)}.
	 */
	@Test
	public final void testCreateNewCounter_InvalidOperator() {
		Counter counter = new Counter();
		counter.setCurrentOperator(invalidOperatorId);
		counter.setServicingType(stubbedCustomerType);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_OPERATOR_NOT_EXIST);
		counter = counterService.createNewCounter(stubbedBranchManagerId, stubbedBranchId, counter);
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignOperator(java.lang.String, java.lang.Long, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public final void testAssignOperator_Valid() {
		Counter counter = counterService.assignOperator(stubbedBranchManagerId, stubbedBranchId, stubbedCounterNumber, stubbedNewOperatorId);
		Assert.assertNotNull("Returned counter should not be null", counter);
		Assert.assertTrue("Counter should have assigned operator after assignment", counter.getCurrentOperator().equalsIgnoreCase(stubbedNewOperatorId));
		Assert.assertTrue("Updated counter should have track of its updation", counter.getLastModifiedBy() == stubbedBranchManagerId);
		Assert.assertNotNull("Counter should have tokens as it had prior to change in assignement", counter.hasToken(stubbedTokenNumber));
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignOperator(java.lang.String, java.lang.Long, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public final void testAssignOperator_InvalidBranchId() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		counterService.assignOperator(stubbedBranchManagerId, nonExistingBranchId, stubbedCounterNumber, stubbedNewOperatorId);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignOperator(java.lang.String, java.lang.Long, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public final void testAssignOperator_WithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		counterService.assignOperator(unauthorizedManagerId, stubbedBranchId, stubbedCounterNumber, stubbedNewOperatorId);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignOperator(java.lang.String, java.lang.Long, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public final void testAssignOperator_InvalidOperator() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_OPERATOR_NOT_EXIST);
		counterService.assignOperator(stubbedBranchManagerId, stubbedBranchId, stubbedCounterNumber, invalidOperatorId);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignOperator(java.lang.String, java.lang.Long, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public final void testAssignOperator_InvalidCounter() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_COUNTER_NOT_EXIST);
		counterService.assignOperator(stubbedBranchManagerId, stubbedBranchId, nonExistingCounterNumber, stubbedNewOperatorId);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignSteps(java.lang.String, java.lang.Long, java.lang.Integer, java.util.List)}.
	 */
	@Test
	public final void testAssignSteps_ValidSingleStep() {
		List<ServiceStep> stepList = getServiceStepsFromIds(Arrays.asList(stubbedStepIds.get(0)));
		counterService.assignSteps(stubbedBranchManagerId, stubbedBranchId, stubbedCounterNumber, stepList);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignSteps(java.lang.String, java.lang.Long, java.lang.Integer, java.util.List)}.
	 */
	@Test
	public final void testAssignSteps_ValidMultipleStep() {
		List<ServiceStep> stepList = getServiceStepsFromIds(stubbedStepIds);
		counterService.assignSteps(stubbedBranchManagerId, stubbedBranchId, stubbedCounterNumber, stepList);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignSteps(java.lang.String, java.lang.Long, java.lang.Integer, java.util.List)}.
	 */
	@Test
	public final void testAssignSteps_InvalidSingleStep() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_SERVICE_STEP_NOT_EXIST);
		List<ServiceStep> stepList = getServiceStepsFromIds(Arrays.asList(nonExistingStepIds.get(0)));
		counterService.assignSteps(stubbedBranchManagerId, stubbedBranchId, stubbedCounterNumber, stepList);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignSteps(java.lang.String, java.lang.Long, java.lang.Integer, java.util.List)}.
	 */
	@Test
	public final void testAssignSteps_InvalidMultipleStep() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_SERVICE_STEP_NOT_EXIST);
		List<ServiceStep> stepList = getServiceStepsFromIds(nonExistingStepIds);
		counterService.assignSteps(stubbedBranchManagerId, stubbedBranchId, stubbedCounterNumber, stepList);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignSteps(java.lang.String, java.lang.Long, java.lang.Integer, java.util.List)}.
	 */
	@Test
	public final void testAssignSteps_MixedValidInvalidStep() {
		List<Long> stepIdList = new ArrayList<>();
		stepIdList.addAll(stubbedStepIds);
		stepIdList.addAll(nonExistingStepIds);
		List<ServiceStep> stepList = getServiceStepsFromIds(stepIdList);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_SERVICE_STEP_NOT_EXIST);
		counterService.assignSteps(stubbedBranchManagerId, stubbedBranchId, stubbedCounterNumber, stepList);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignSteps(java.lang.String, java.lang.Long, java.lang.Integer, java.util.List)}.
	 */
	@Test
	public final void testAssignSteps_NullStep() {
		List<ServiceStep> stepList = getServiceStepsFromIds(stubbedStepIds);
		stepList.add(getServiceStepsFromId(null));
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_INVALID_SERVICE_STEP_ID);
		counterService.assignSteps(stubbedBranchManagerId, stubbedBranchId, stubbedCounterNumber, stepList);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignSteps(java.lang.String, java.lang.Long, java.lang.Integer, java.util.List)}.
	 */
	@Test
	public final void testAssignSteps_InvalidCounter() {
		List<ServiceStep> stepList = getServiceStepsFromIds(stubbedStepIds);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_COUNTER_NOT_EXIST);
		counterService.assignSteps(stubbedBranchManagerId, stubbedBranchId, nonExistingCounterNumber, stepList);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignSteps(java.lang.String, java.lang.Long, java.lang.Integer, java.util.List)}.
	 */
	@Test
	public final void testAssignSteps_WithoutAccess() {
		List<ServiceStep> stepList = getServiceStepsFromIds(stubbedStepIds);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		counterService.assignSteps(unauthorizedManagerId, stubbedBranchId, stubbedCounterNumber, stepList);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignSteps(java.lang.String, java.lang.Long, java.lang.Integer, java.util.List)}.
	 */
	@Test
	public final void testAssignSteps_InvalidBranchId() {
		List<ServiceStep> stepList = getServiceStepsFromIds(stubbedStepIds);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		counterService.assignSteps(stubbedBranchManagerId, nonExistingBranchId, stubbedCounterNumber, stepList);
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#serviceFirstCounter(java.lang.String, java.lang.Long, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public final void testServiceFirstCounter_Valid() {
		counterService.serviceFirstCounter(stubbedOperatorId, stubbedBranchId, stubbedCounterNumber, "");
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#serviceFirstCounter(java.lang.String, java.lang.Long, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public final void testServiceFirstCounter_InvalidBranchId() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		counterService.serviceFirstCounter(stubbedOperatorId, nonExistingBranchId, stubbedCounterNumber, "");
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#serviceFirstCounter(java.lang.String, java.lang.Long, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public final void testServiceFirstCounter_InvalidCounter() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_COUNTER_NOT_EXIST);
		counterService.serviceFirstCounter(stubbedOperatorId, stubbedBranchId, nonExistingCounterNumber, "");
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#serviceFirstCounter(java.lang.String, java.lang.Long, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public final void testServiceFirstCounter_WithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		counterService.serviceFirstCounter(invalidOperatorId, stubbedBranchId, stubbedCounterNumber, "");
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#serviceFirstCounter(java.lang.String, java.lang.Long, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public final void testServiceFirstCounter_WhenEmpty() {
		counterService.serviceFirstCounter(stubbedOperatorId, stubbedBranchId, stubbedCounterNumber, "");
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_EMPTY_COUNTER_QUEUE);
		counterService.serviceFirstCounter(stubbedOperatorId, stubbedBranchId, stubbedCounterNumber, "");
	}
}
