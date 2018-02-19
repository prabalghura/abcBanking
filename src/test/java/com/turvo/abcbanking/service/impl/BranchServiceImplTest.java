/**
 * 
 */
package com.turvo.abcbanking.service.impl;

import java.util.Arrays;
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
import com.turvo.abcbanking.model.Branch;
import com.turvo.abcbanking.model.User;
import com.turvo.abcbanking.repository.BranchRepository;
import com.turvo.abcbanking.service.BranchService;
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
	RoleService roleService;
	
	@MockBean
	UserService userService;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#getAllBranches()}.
	 */
	@Test
	public final void testGetAllBranchesIfEmpty() {
		Assert.assertTrue("Branches should be empty", branchService.getAllBranches().isEmpty());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#getAllBranches()}.
	 */
	@Test
	public final void testGetAllBranchesIfNotEmpty() {
		Mockito.when(branchRepository.findAll()).thenReturn(Arrays.asList(new Branch()));
		Assert.assertFalse("Branches should not be empty", branchService.getAllBranches().isEmpty());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#getBranch(java.lang.Long)}.
	 */
	@Test
	public final void testGetBranchIfUndefined() {
		Assert.assertTrue("Undefined branch should be null", Objects.isNull(branchService.getBranch(1L)));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#getBranch(java.lang.Long)}.
	 */
	@Test
	public final void testGetBranchIfDefined() {
		Mockito.when(branchRepository.findOne(1L)).thenReturn(new Branch());
		Assert.assertFalse("Defined branch should not be null", Objects.isNull(branchService.getBranch(1L)));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#createNewBranch(java.lang.String, com.turvo.abcbanking.model.Branch)}.
	 */
	@Test
	public final void testCreateNewBranchWithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		branchService.createNewBranch("userId", new Branch());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#createNewBranch(java.lang.String, com.turvo.abcbanking.model.Branch)}.
	 */
	@Test
	public final void testCreateNewBranchWithAccessInvalidManager() {
		Mockito.when(roleService.checkAccessForUser("userId", ApplicationConstants.ROLE_ADD_NEW_BRANCH)).thenReturn(true);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_MANAGER_NOT_EXIST);
		branchService.createNewBranch("userId", new Branch());
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#assignManager(java.lang.String, java.lang.Long, java.lang.String)}.
	 */
	@Test
	public final void testAssignManagerWithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		branchService.assignManager("userId", 1L, "managerId");
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#assignManager(java.lang.String, java.lang.Long, java.lang.String)}.
	 */
	@Test
	public final void testAssignManagerWithAccessInvalidManager() {
		Mockito.when(roleService.checkAccessForUser("userId", ApplicationConstants.ROLE_ADD_NEW_BRANCH)).thenReturn(true);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_MANAGER_NOT_EXIST);
		branchService.assignManager("userId", 1L, "managerId");
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.BranchServiceImpl#assignManager(java.lang.String, java.lang.Long, java.lang.String)}.
	 */
	@Test
	public final void testAssignManagerWithAccessValidManagerInvalidBranch() {
		Mockito.when(roleService.checkAccessForUser("userId", ApplicationConstants.ROLE_ADD_NEW_BRANCH)).thenReturn(true);
		Mockito.when(userService.getUser("managerId")).thenReturn(new User());
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		branchService.assignManager("userId", 1L, "managerId");
	}
}
