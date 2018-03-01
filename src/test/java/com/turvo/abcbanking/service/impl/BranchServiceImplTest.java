package com.turvo.abcbanking.service.impl;

import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

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
import com.turvo.abcbanking.model.Branch;
import com.turvo.abcbanking.model.Counter;
import com.turvo.abcbanking.model.CustomerType;
import com.turvo.abcbanking.model.Service;
import com.turvo.abcbanking.model.ServiceStep;
import com.turvo.abcbanking.model.Token;
import com.turvo.abcbanking.model.User;
import com.turvo.abcbanking.repository.BranchRepository;
import com.turvo.abcbanking.repository.ServiceRepository;
import com.turvo.abcbanking.service.BranchService;
import com.turvo.abcbanking.service.CounterService;
import com.turvo.abcbanking.service.RoleService;
import com.turvo.abcbanking.service.UserService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Test class for Branch service
 * 
 * @author Prabal Ghura
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BranchServiceImplTest {

	@Autowired
	BranchService branchService;
	
	@MockBean
	BranchRepository branchRepository;
	
	@MockBean
	CounterService counterService;
	
	@MockBean
	RoleService roleService;
	
	@MockBean
	UserService userService;
	
	@MockBean
	ServiceRepository serviceRepository;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// These are configurable settings
	
	// stubbedBranchId & nonExistingBranchId should be mutually exclusive
	Long stubbedBranchId = 1L;
	Long nonExistingBranchId = 2L;
	
	String stubbedBranchManagerId = "branchManagerId";
	
	// existingUserId & nonExistentUserId should be mutually exclusive
	String existingUserId = "validUserId";
	String nonExistentUserId = "invalidUserId";
	
	// commonStepId, nonExistingStepId & distinctStepIds should have mutually exclusive values
	Long commonStepId = 1L;
	// distinctStepIds should have greater than or equal elements as stubbedRegularCounterNumbers & stubbedPremiumCounterNumbers
	List<Long> distinctStepIds = Arrays.asList(2L, 3L);
	Long nonExistingStepId = 4L;
	
	// stubbedRegularCounterNumbers, stubbedPremiumCounterNumbers & nonExistingCounterNumbers should have unique mutually exclusive values
	List<Integer> stubbedRegularCounterNumbers = Arrays.asList(1, 2);
	List<Integer> stubbedPremiumCounterNumbers = Arrays.asList(3, 4);
	List<Integer> nonExistingCounterNumbers = Arrays.asList(5, 6);
	
	// userWithAccess & userWithoutAccess should be mutually exclusive
	String userWithAccess = "userWithAccess";
	String userWithoutAccess = "userWithoutAccess";
	
	private Counter createCounter(Integer counterNumber, CustomerType type, Long... steps) {
		Counter counter = new Counter();
		counter.setId(counterNumber.longValue());
		counter.setBranchId(stubbedBranchId);
		counter.setNumber(counterNumber);
		counter.setServicingType(type);
		List<ServiceStep> stepList = new ArrayList<>();
		for(Long id: steps) {
			ServiceStep step = new ServiceStep();
			step.setId(id);
			stepList.add(step);
		}
		counter.setSteps(stepList);
		return counter;
	}
	
	/**
	 * Stubbing all dependencies at one place
	 */
	@Before
	public final void stubDependencies() {
		List<Counter> counters = new ArrayList<>();
		int distinctStepCounter = 0;
		for(Integer id: stubbedRegularCounterNumbers) {
			counters.add(createCounter(id, CustomerType.REGULAR, commonStepId, distinctStepIds.get(distinctStepCounter)));
			distinctStepCounter++;
		}
		distinctStepCounter = 0;
		for(Integer id: stubbedPremiumCounterNumbers) {
			counters.add(createCounter(id, CustomerType.PREMIUM, commonStepId, distinctStepIds.get(distinctStepCounter)));
			distinctStepCounter++;
		}
		
		Branch branch = new Branch();
		branch.setId(stubbedBranchId);
		branch.setManagerId(stubbedBranchManagerId);
		
		Mockito.when(branchRepository.findAll()).thenReturn(Arrays.asList(branch));
		Mockito.when(branchRepository.findOne(stubbedBranchId)).thenReturn(branch);
		Mockito.when(counterService.getBranchCountersFromDB(stubbedBranchId)).thenReturn(counters);
		Mockito.when(userService.getUser(existingUserId)).thenReturn(new User());
		Mockito.when(roleService.checkAccessForUser(existingUserId, ApplicationConstants.ROLE_MANAGER)).thenReturn(true);
		Mockito.when(roleService.checkAccessForUser(userWithAccess, ApplicationConstants.ROLE_ADD_NEW_BRANCH)).thenReturn(true);
		Mockito.when(branchRepository.saveAndFlush(any(Branch.class))).then(AdditionalAnswers.returnsFirstArg());
		branchService.reloadEntireCache();
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#getAllBranches()}.
	 */
	@Test
	public final void testGetAllBranches_Valid() {
		List<Branch> branches = branchService.getAllBranches();
		Assert.assertFalse("Branch list should not be empty", branches.isEmpty());
		Assert.assertNotNull("Branch list should have stubbed instance", branches.get(0));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#getBranch(java.lang.Long)}.
	 */
	@Test
	public final void testGetBranch_Valid() {
		Branch branch = branchService.getBranch(stubbedBranchId);
		Assert.assertNotNull("Stubbed Branch should be retrieved", branch);
		Assert.assertNull("Non Existing Branch should not be retrieved", branchService.getBranch(nonExistingBranchId));
		Assert.assertTrue("Stubbed branch should have only stubbed counters", branch.getCounters()
				.stream().allMatch(counter -> stubbedRegularCounterNumbers.contains(counter.getNumber())
				|| stubbedPremiumCounterNumbers.contains(counter.getNumber())));
		Assert.assertTrue("Stubbed branch should not have non existing counters", branch.getCounters().stream()
				.noneMatch(counter -> nonExistingCounterNumbers.contains(counter.getNumber())));
		Assert.assertTrue("All counters should be serving common step id", branch.getCounters().stream()
				.allMatch(counter1 -> counter1.getSteps().stream().anyMatch(step -> step.getId() == commonStepId)));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#createNewBranch(java.lang.String, com.turvo.abcbanking.model.Branch)}.
	 */
	@Test
	public final void testCreateNewBranch_Valid() {
		Assert.assertNull("Initially branch should be non existent", branchService.getBranch(nonExistingBranchId));
		Branch branch = new Branch();
		branch.setManagerId(existingUserId);
		// doing this step just because we have mocked branchRepository and it will return result without any modification.
		branch.setId(nonExistingBranchId);
		branchService.createNewBranch(userWithAccess, branch);
		branch = branchService.getBranch(nonExistingBranchId);
		Assert.assertNotNull("Branch should exist after creation", branch);
		Assert.assertTrue("Branch should be exist after creation", branch.getManagerId().equalsIgnoreCase(existingUserId));
		Assert.assertTrue("Branch should have tracked its creation", branch.getLastModifiedBy().equalsIgnoreCase(userWithAccess));
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#createNewBranch(java.lang.String, com.turvo.abcbanking.model.Branch)}.
	 */
	@Test
	public final void testCreateNewBranch_InvalidManager() {
		Branch branch = new Branch();
		branch.setManagerId(nonExistentUserId);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_MANAGER_NOT_EXIST);
		branchService.createNewBranch(userWithAccess, branch);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#createNewBranch(java.lang.String, com.turvo.abcbanking.model.Branch)}.
	 */
	@Test
	public final void testCreateNewBranch_WithoutAccess() {
		Branch branch = new Branch();
		branch.setManagerId(existingUserId);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		branchService.createNewBranch(userWithoutAccess, branch);
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#assignManager(java.lang.String, java.lang.Long, java.lang.String)}.
	 */
	@Test
	public final void testAssignManager_Valid() {
		Branch branch = branchService.getBranch(stubbedBranchId);
		Assert.assertTrue("Initially branch should have stubbed manager", branch.getManagerId().equalsIgnoreCase(stubbedBranchManagerId));
		
		branch = branchService.assignManager(userWithAccess, stubbedBranchId, existingUserId);
		Predicate<Counter> counterMatch = counter -> stubbedRegularCounterNumbers.contains(counter.getNumber())
				|| stubbedPremiumCounterNumbers.contains(counter.getNumber());
		Predicate<Counter> counterNonMatch = counter -> nonExistingCounterNumbers.contains(counter.getNumber());
		Predicate<ServiceStep> stepMatch = step -> step.getId() == commonStepId;
		
		Assert.assertTrue("Branch manager should have been changed with passed user", branch.getManagerId().equalsIgnoreCase(existingUserId));
		Assert.assertTrue("Branch should have tracked its modification", branch.getLastModifiedBy().equalsIgnoreCase(userWithAccess));
		Assert.assertTrue("Stubbed counters should not have been changed", branch.getCounters().stream().allMatch(counterMatch));
		Assert.assertTrue("Stubbed counters should not have been changed", branch.getCounters().stream().noneMatch(counterNonMatch));
		Assert.assertTrue("Stubbed service steps for counters should not have been changed", branch.getCounters().stream()
				.allMatch(counter1 -> counter1.getSteps().stream().anyMatch(stepMatch)));
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#assignManager(java.lang.String, java.lang.Long, java.lang.String)}.
	 */
	@Test
	public final void testAssignManager_WithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		branchService.assignManager(userWithoutAccess, stubbedBranchId, existingUserId);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#assignManager(java.lang.String, java.lang.Long, java.lang.String)}.
	 */
	@Test
	public final void testAssignManager_InvalidBranch() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		branchService.assignManager(userWithAccess, nonExistingBranchId, existingUserId);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#assignManager(java.lang.String, java.lang.Long, java.lang.String)}.
	 */
	@Test
	public final void testAssignManager_InvalidManager() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_MANAGER_NOT_EXIST);
		branchService.assignManager(userWithAccess, stubbedBranchId, nonExistentUserId);
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#updateBranch(java.lang.String, java.lang.Long)}.
	 */
	@Test
	public final void testUpdateBranch_Valid() {
		/* Mocking service repository to return regular services for a branch, 
		this represents a change in DB which we want our cache to refresh with */
		Service service = new Service();
		service.setId(1L);
		Mockito.when(serviceRepository.getServicesForBranch(stubbedBranchId, CustomerType.REGULAR)).thenReturn(Arrays.asList(service));
		
		Branch branch = branchService.getBranch(stubbedBranchId);
		Assert.assertTrue("Initially branch should not serve any service because we haven't stubbed any", branch.getRegularServices().isEmpty());
		
		branch = branchService.updateBranch(stubbedBranchManagerId, stubbedBranchId);
		Assert.assertTrue("After updation branch should serve service updated in DB", branch.getRegularServices().stream().anyMatch(service1 -> service1.getId() == 1L));
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#updateBranch(java.lang.String, java.lang.Long)}.
	 */
	@Test
	public final void testUpdateBranch_WithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		branchService.updateBranch(existingUserId, stubbedBranchId);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#updateBranch(java.lang.String, java.lang.Long)}.
	 */
	@Test
	public final void testUpdateBranch_InvalidBranch() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		branchService.updateBranch(stubbedBranchManagerId, nonExistingBranchId);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#updateBranch(java.lang.String, java.lang.Long)}.
	 */
	@Test
	public final void testUpdateBranch_WhenDBManagerChanged() {
		// Mocking that branch manager has changed in DB
		Branch branch = branchRepository.findOne(stubbedBranchId);
		branch.setManagerId(existingUserId);
		Mockito.when(branchRepository.findOne(stubbedBranchId)).thenReturn(branch);
		
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		branchService.updateBranch(stubbedBranchManagerId, stubbedBranchId);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#updateBranch(java.lang.String, java.lang.Long)}.
	 */
	@Test
	public final void testUpdateBranch_WhenDBManagerChangedValid() {
		/* Mocking service repository to return regular services for a branch, 
		this represents a change in DB which we want our cache to refresh with */
		Service service = new Service();
		service.setId(1L);
		Mockito.when(serviceRepository.getServicesForBranch(stubbedBranchId, CustomerType.REGULAR)).thenReturn(Arrays.asList(service));
		
		// Mocking that branch manager has changed in DB
		Branch branch = branchRepository.findOne(stubbedBranchId);
		branch.setManagerId(existingUserId);
		Mockito.when(branchRepository.findOne(stubbedBranchId)).thenReturn(branch);
		
		branch = branchService.getBranch(stubbedBranchId);
		Assert.assertTrue("Initially branch should not serve any service because we haven't stubbed any", branch.getRegularServices().isEmpty());
		
		branch = branchService.updateBranch(existingUserId, stubbedBranchId);
		Assert.assertTrue("After updation branch should serve service updated in DB", branch.getRegularServices().stream().anyMatch(service1 -> service1.getId() == 1L));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#updateCounter(com.turvo.abcbanking.model.Counter)}.
	 */
	@Test
	public final void testUpdateCounter_Valid() {
		Counter counter = branchService.getBranch(stubbedBranchId).getCounter(stubbedRegularCounterNumbers.get(0));
		Assert.assertTrue("Initially counter should not have any token because we haven't stubbed any", counter.getTokens().size() == 0);
		
		counter.addToken(new Token());
		branchService.updateCounter(counter);
		
		counter = branchService.getBranch(stubbedBranchId).getCounter(stubbedRegularCounterNumbers.get(0));
		Assert.assertFalse("Counter should have token reflected in cache", counter.getTokens().size() == 0);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#updateCounter(com.turvo.abcbanking.model.Counter)}.
	 */
	@Test
	public final void testUpdateCounter_NewCounterValid() {
		Counter counter = branchService.getBranch(stubbedBranchId).getCounter(nonExistingCounterNumbers.get(0));
		Assert.assertNull("Initially branch should not have counters that we haven't stubbed", counter);
		
		counter = createCounter(nonExistingCounterNumbers.get(0), CustomerType.REGULAR);
		branchService.updateCounter(counter);
		
		counter = branchService.getBranch(stubbedBranchId).getCounter(nonExistingCounterNumbers.get(0));
		Assert.assertNotNull("Branch should now have non existing counter because it has been created");
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#updateCounter(com.turvo.abcbanking.model.Counter)}.
	 */
	@Test
	public final void testUpdateCounter_InvalidBranch() {
		Counter counter = new Counter();
		counter.setBranchId(nonExistingBranchId);
		
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		branchService.updateCounter(counter);
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#getBestCounter(java.lang.Long, com.turvo.abcbanking.model.CustomerType, java.lang.Long)}.
	 */
	@Test
	public final void testGetBestCounter_InvalidStep() {
		Counter counter = branchService.getBestCounter(stubbedBranchId, CustomerType.REGULAR, nonExistingStepId);
		Assert.assertNull("No regular counter should be serving non stubbed service step", counter);
		counter = branchService.getBestCounter(stubbedBranchId, CustomerType.PREMIUM, nonExistingStepId);
		Assert.assertNull("No premium counter should be serving non stubbed service step", counter);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#getBestCounter(java.lang.Long, com.turvo.abcbanking.model.CustomerType, java.lang.Long)}.
	 */
	@Test
	public final void testGetBestCounter_InvalidBranch() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		branchService.getBestCounter(nonExistingBranchId, CustomerType.REGULAR, commonStepId);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#getBestCounter(java.lang.Long, com.turvo.abcbanking.model.CustomerType, java.lang.Long)}.
	 */
	@Test
	public final void testGetBestCounter_CommonStepRegular() {
		Counter counter = branchService.getBestCounter(stubbedBranchId, CustomerType.REGULAR, commonStepId);
		Assert.assertTrue("First regular counter should be returned", counter.getNumber() == stubbedRegularCounterNumbers.get(0));
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#getBestCounter(java.lang.Long, com.turvo.abcbanking.model.CustomerType, java.lang.Long)}.
	 */
	@Test
	public final void testGetBestCounter_CommonStepRegularRepeated() {
		Counter counter;
		for(int i=0;i<50;i++) {
			counter = branchService.getBestCounter(stubbedBranchId, CustomerType.REGULAR, commonStepId);
			Assert.assertTrue("Regular Counters should be returned in a round robin fashion", 
					counter.getNumber() == stubbedRegularCounterNumbers.get(i%stubbedRegularCounterNumbers.size()));
			counter.addToken(new Token());
			branchService.updateCounter(counter);
		}
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#getBestCounter(java.lang.Long, com.turvo.abcbanking.model.CustomerType, java.lang.Long)}.
	 */
	@Test
	public final void testGetBestCounter_DistinctStepRegularRepeated() {
		Counter counter;
		Long randomDistinctStepId = distinctStepIds.get(new Random().nextInt(distinctStepIds.size()));
		List<Counter> counters = new ArrayList<>();
		for(int i=0;i<50;i++) {
			counter = branchService.getBestCounter(stubbedBranchId, CustomerType.REGULAR, randomDistinctStepId);
			counter.addToken(new Token());
			counters.add(branchService.updateCounter(counter));
		}
		Integer counterNumber = counters.get(0).getNumber();
		Assert.assertTrue("All the tokens should be assigned to same counter", counters.stream()
				.allMatch(counter1 -> counter1.getNumber() == counterNumber));
		Assert.assertTrue("Assigned counter should be a regular counter", stubbedRegularCounterNumbers.stream()
				.anyMatch(number -> number == counterNumber));
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#getBestCounter(java.lang.Long, com.turvo.abcbanking.model.CustomerType, java.lang.Long)}.
	 */
	@Test
	public final void testGetBestCounter_CommonStepPremium() {
		Counter counter = branchService.getBestCounter(stubbedBranchId, CustomerType.PREMIUM, commonStepId);
		Assert.assertTrue("First premium counter should be returned", counter.getNumber() == stubbedPremiumCounterNumbers.get(0));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#getBestCounter(java.lang.Long, com.turvo.abcbanking.model.CustomerType, java.lang.Long)}.
	 */
	@Test
	public final void testGetBestCounter_CommonStepPremiumRepeated() {
		Counter counter;
		for(int i=0;i<50;i++) {
			counter = branchService.getBestCounter(stubbedBranchId, CustomerType.PREMIUM, commonStepId);
			Assert.assertTrue("Premium Counters should be returned in a round robin fashion", 
					counter.getNumber() == stubbedPremiumCounterNumbers.get(i%stubbedPremiumCounterNumbers.size()));
			counter.addToken(new Token());
			branchService.updateCounter(counter);
		}
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#getBestCounter(java.lang.Long, com.turvo.abcbanking.model.CustomerType, java.lang.Long)}.
	 */
	@Test
	public final void testGetBestCounter_DistinctStepPremiumRepeated() {
		Counter counter;
		Long randomDistinctStepId = distinctStepIds.get(new Random().nextInt(distinctStepIds.size()));
		List<Counter> counters = new ArrayList<>();
		for(int i=0;i<50;i++) {
			counter = branchService.getBestCounter(stubbedBranchId, CustomerType.PREMIUM, randomDistinctStepId);
			counter.addToken(new Token());
			counters.add(branchService.updateCounter(counter));
		}
		Integer counterNumber = counters.get(0).getNumber();
		Assert.assertTrue("All the tokens should be assigned to same counter", counters.stream()
				.allMatch(counter1 -> counter1.getNumber() == counterNumber));
		Assert.assertTrue("Assigned counter should be a premium counter", stubbedPremiumCounterNumbers.stream()
				.anyMatch(number -> number == counterNumber));
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#reloadEntireCache()}.
	 */
	@Test
	public final void testReloadEntireCache() {
		/* Mocking service repository to return regular services for a branch, 
		this represents a change in DB which we want our cache to refresh with */
		Service service = new Service();
		service.setId(1L);
		Mockito.when(serviceRepository.getServicesForBranch(stubbedBranchId, CustomerType.REGULAR)).thenReturn(Arrays.asList(service));
		
		Branch branch = branchService.getBranch(stubbedBranchId);
		Assert.assertTrue("Initially branch should not serve any service because we haven't stubbed any", branch.getRegularServices().isEmpty());
		
		branchService.reloadEntireCache();
		branch = branchService.getBranch(stubbedBranchId);
		Assert.assertTrue("After updation branch should serve service updated in DB", branch.getRegularServices().stream()
				.anyMatch(service1 -> service1.getId() == 1L));
		
		// resetting cache
		Mockito.when(serviceRepository.getServicesForBranch(stubbedBranchId, CustomerType.REGULAR)).thenReturn(new ArrayList<>());
		branchService.reloadEntireCache();
	}
}