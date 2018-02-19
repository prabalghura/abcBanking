/**
 * 
 */
package com.turvo.abcbanking.service.impl;

import java.util.ArrayList;
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
import com.turvo.abcbanking.repository.RoleRepository;
import com.turvo.abcbanking.repository.UserRepository;
import com.turvo.abcbanking.repository.UserXRoleRepository;
import com.turvo.abcbanking.service.RoleService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Test class for Role service
 * 
 * @author Prabal Ghura
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleServiceImplTest {

	@Autowired
	RoleService roleService;
	
	@MockBean
	UserRepository userRepository;
	
	@MockBean
	RoleRepository roleRepository;
	
	@MockBean
	UserXRoleRepository userXroleRepository;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#checkAccessForUser(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testCheckAccessForUserIfAbsent() {
		Mockito.when(roleRepository.checkAccess("userId", "role")).thenReturn(0);
		Assert.assertFalse("Access should not be present", roleService.checkAccessForUser("userId", "role"));
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#checkAccessForUser(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testCheckAccessForUserIfPresent() {
		Mockito.when(roleRepository.checkAccess("userId", "role")).thenReturn(1);
		Assert.assertTrue("Access should not be present", roleService.checkAccessForUser("userId", "role"));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#getUserRoles(java.lang.String)}.
	 */
	@Test
	public final void testGetUserRolesInvalidUser() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_USER_NOT_EXIST);
		roleService.getUserRoles("userId");
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#getUserRoles(java.lang.String)}.
	 */
	@Test
	public final void testGetUserRolesValid() {
		Mockito.when(userRepository.exists("userId")).thenReturn(true);
		Mockito.when(roleRepository.getRolesForUser("userId")).thenReturn(new ArrayList<>());
		Assert.assertFalse("User roles should not be null", Objects.isNull(roleService.getUserRoles("userId")));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#assignRolesToUser(java.lang.String, java.lang.String, java.util.List)}.
	 */
	@Test
	public final void testAssignRolesToUserWithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		roleService.assignRolesToUser("assignerId", "userId", new ArrayList<>());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#assignRolesToUser(java.lang.String, java.lang.String, java.util.List)}.
	 */
	@Test
	public final void testAssignRolesToUserInvalidUser() {
		Mockito.when(roleRepository.checkAccess("assignerId", ApplicationConstants.ROLE_ASSIGN_ROLES)).thenReturn(1);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_USER_NOT_EXIST);
		roleService.assignRolesToUser("assignerId", "userId", new ArrayList<>());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#assignRolesToUser(java.lang.String, java.lang.String, java.util.List)}.
	 */
	@Test
	public final void testAssignRolesToUserValid() {
		Mockito.when(roleRepository.checkAccess("assignerId", ApplicationConstants.ROLE_ASSIGN_ROLES)).thenReturn(1);
		Mockito.when(userRepository.exists("userId")).thenReturn(true);
		roleService.assignRolesToUser("assignerId", "userId", new ArrayList<>());
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#getAllRoles()}.
	 */
	@Test
	public final void testGetAllRoles() {
		Mockito.when(roleRepository.findAll()).thenReturn(new ArrayList<>());
		Assert.assertFalse("Roles should not be null", Objects.isNull(roleService.getAllRoles()));
	}
}
