/**
 * 
 */
package com.turvo.abcbanking.service.impl;

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
import com.turvo.abcbanking.model.Branch;
import com.turvo.abcbanking.model.Counter;
import com.turvo.abcbanking.model.User;
import com.turvo.abcbanking.repository.CounterRepository;
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
	
	@Rule
	public ExpectedException exception = ExpectedException.none();

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#createNewCounter(java.lang.String, java.lang.Long, com.turvo.abcbanking.model.Counter)}.
	 */
	@Test
	public final void testCreateNewCounterInvalidBranch() {
		Branch branch = new Branch();
		branch.setManagerId("userId");
		Counter counter = new Counter();
		counter.setCurrentOperator("operator1");
		Mockito.when(branchService.getBranch(1L)).thenReturn(null);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		counterService.createNewCounter("userId", 1L, counter);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#createNewCounter(java.lang.String, java.lang.Long, com.turvo.abcbanking.model.Counter)}.
	 */
	@Test
	public final void testCreateNewCounterWithoutAccess() {
		Branch branch = new Branch();
		branch.setManagerId("user2");
		Counter counter = new Counter();
		counter.setCurrentOperator("operator1");
		Mockito.when(branchService.getBranch(1L)).thenReturn(branch);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		counterService.createNewCounter("userId", 1L, counter);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#createNewCounter(java.lang.String, java.lang.Long, com.turvo.abcbanking.model.Counter)}.
	 */
	@Test
	public final void testCreateNewCounterInvalidOperator() {
		Branch branch = new Branch();
		branch.setManagerId("userId");
		Counter counter = new Counter();
		counter.setCurrentOperator(null);
		Mockito.when(branchService.getBranch(1L)).thenReturn(branch);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_OPERATOR_NOT_EXIST);
		counterService.createNewCounter("userId", 1L, counter);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#createNewCounter(java.lang.String, java.lang.Long, com.turvo.abcbanking.model.Counter)}.
	 */
	@Test
	public final void testCreateNewCounterValid() {
		Branch branch = new Branch();
		branch.setManagerId("userId");
		Counter counter = new Counter();
		counter.setCurrentOperator("operator1");
		Mockito.when(branchService.getBranch(1L)).thenReturn(branch);
		Mockito.when(userService.getUser("operator1")).thenReturn(new User());
		counterService.createNewCounter("userId", 1L, counter);
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignOperator(java.lang.String, java.lang.Long, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public final void testAssignOperatorInvalidBranch() {
		Branch branch = new Branch();
		branch.setId(1L);
		branch.setManagerId("userId");
		Mockito.when(branchService.getBranch(1L)).thenReturn(null);
		Mockito.when(userService.getUser("operator1")).thenReturn(new User());
		Mockito.when(counterRepository.findFirstByBranchIdAndNumber(1L, 1)).thenReturn(new Counter());
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		counterService.assignOperator("userId", 1L, 1, "operator1");
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignOperator(java.lang.String, java.lang.Long, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public final void testAssignOperatorWithoutAccess() {
		Branch branch = new Branch();
		branch.setId(1L);
		branch.setManagerId("user2");
		Mockito.when(branchService.getBranch(1L)).thenReturn(branch);
		Mockito.when(userService.getUser("operator1")).thenReturn(new User());
		Mockito.when(counterRepository.findFirstByBranchIdAndNumber(1L, 1)).thenReturn(new Counter());
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		counterService.assignOperator("userId", 1L, 1, "operator1");
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignOperator(java.lang.String, java.lang.Long, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public final void testAssignOperatorInvalidOperator() {
		Branch branch = new Branch();
		branch.setId(1L);
		branch.setManagerId("userId");
		Mockito.when(branchService.getBranch(1L)).thenReturn(branch);
		Mockito.when(userService.getUser("operator1")).thenReturn(null);
		Mockito.when(counterRepository.findFirstByBranchIdAndNumber(1L, 1)).thenReturn(new Counter());
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_OPERATOR_NOT_EXIST);
		counterService.assignOperator("userId", 1L, 1, "operator1");
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignOperator(java.lang.String, java.lang.Long, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public final void testAssignOperatorInvalidCounter() {
		Branch branch = new Branch();
		branch.setId(1L);
		branch.setManagerId("userId");
		Mockito.when(branchService.getBranch(1L)).thenReturn(branch);
		Mockito.when(userService.getUser("operator1")).thenReturn(new User());
		Mockito.when(counterRepository.findFirstByBranchIdAndNumber(1L, 1)).thenReturn(null);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_COUNTER_NOT_EXIST);
		counterService.assignOperator("userId", 1L, 1, "operator1");
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.CounterServiceImpl#assignOperator(java.lang.String, java.lang.Long, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public final void testAssignOperatorValid() {
		Branch branch = new Branch();
		branch.setId(1L);
		branch.setManagerId("userId");
		Mockito.when(branchService.getBranch(1L)).thenReturn(branch);
		Mockito.when(userService.getUser("operator1")).thenReturn(new User());
		Mockito.when(counterRepository.findFirstByBranchIdAndNumber(1L, 1)).thenReturn(new Counter());
		counterService.assignOperator("userId", 1L, 1, "operator1");
	}
}
