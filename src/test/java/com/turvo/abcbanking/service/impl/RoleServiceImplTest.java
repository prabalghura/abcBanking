package com.turvo.abcbanking.service.impl;

import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
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
	
	// These are configurable settings
	
	// stubbedUserId and nonExistingUserId should be mutually exclusive
	String stubbedUserId = "userIdWithAccess";
	String nonExistingUserId = "userIdWithoutAccess";
	
	String assignedRole = ApplicationConstants.ROLE_ASSIGN_ROLES;
	
	/**
	 * Stubbing all dependencies at one place
	 */
	@Before
	public final void stubDependencies() {
		Mockito.when(roleRepository.checkAccess(anyString(), anyString())).thenReturn(0);
		Mockito.when(roleRepository.checkAccess(stubbedUserId, assignedRole)).thenReturn(1);
		Mockito.when(userRepository.exists(anyString())).thenReturn(false);
		Mockito.when(userRepository.exists(stubbedUserId)).thenReturn(true);
		Mockito.when(roleRepository.getRolesForUser(stubbedUserId)).thenReturn(new ArrayList<>());
		Mockito.when(roleRepository.findAll()).thenReturn(new ArrayList<>());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#checkAccessForUser(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testCheckAccessForUserIfAbsent() {
		Assert.assertFalse("Access should not be present", roleService.checkAccessForUser(nonExistingUserId, assignedRole));
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#checkAccessForUser(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testCheckAccessForUserIfPresent() {
		Assert.assertTrue("Access should be present", roleService.checkAccessForUser(stubbedUserId, assignedRole));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#getUserRoles(java.lang.String)}.
	 */
	@Test
	public final void testGetUserRolesInvalidUser() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_USER_NOT_EXIST);
		roleService.getUserRoles(nonExistingUserId);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#getUserRoles(java.lang.String)}.
	 */
	@Test
	public final void testGetUserRolesValid() {
		Assert.assertFalse("User roles should not be null", Objects.isNull(roleService.getUserRoles(stubbedUserId)));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#assignRolesToUser(java.lang.String, java.lang.String, java.util.List)}.
	 */
	@Test
	public final void testAssignRolesToUserWithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		roleService.assignRolesToUser(nonExistingUserId, nonExistingUserId, new ArrayList<>());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#assignRolesToUser(java.lang.String, java.lang.String, java.util.List)}.
	 */
	@Test
	public final void testAssignRolesToUserInvalidUser() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_USER_NOT_EXIST);
		roleService.assignRolesToUser(stubbedUserId, nonExistingUserId, new ArrayList<>());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#assignRolesToUser(java.lang.String, java.lang.String, java.util.List)}.
	 */
	@Test
	public final void testAssignRolesToUserValid() {
		roleService.assignRolesToUser(stubbedUserId, stubbedUserId, new ArrayList<>());
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.RoleServiceImpl#getAllRoles()}.
	 */
	@Test
	public final void testGetAllRoles() {
		Assert.assertFalse("Roles should not be null", Objects.isNull(roleService.getAllRoles()));
	}
}
