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
import com.turvo.abcbanking.model.User;
import com.turvo.abcbanking.repository.UserRepository;
import com.turvo.abcbanking.service.RoleService;
import com.turvo.abcbanking.service.UserService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Test class for User service
 * 
 * @author Prabal Ghura
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {

	@Autowired
	UserService userService;
	
	@MockBean
	RoleService roleService;
	
	@MockBean
	UserRepository userRepository;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.UserServiceImpl#getAllUsers()}.
	 */
	@Test
	public final void testGetAllUsers() {
		Mockito.when(userRepository.findAll()).thenReturn(new ArrayList<>());
		Assert.assertFalse("User list should not be null", Objects.isNull(userService.getAllUsers()));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.UserServiceImpl#getUser(java.lang.String)}.
	 */
	@Test
	public final void testGetUser() {
		Mockito.when(userRepository.findOne("userId")).thenReturn(new User());
		Assert.assertFalse("User should not be null", Objects.isNull(userService.getUser("userId")));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.UserServiceImpl#createNewUser(java.lang.String, com.turvo.abcbanking.model.User)}.
	 */
	@Test
	public final void testCreateNewUserWithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		userService.createNewUser("userId", new User());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.UserServiceImpl#createNewUser(java.lang.String, com.turvo.abcbanking.model.User)}.
	 */
	@Test
	public final void testCreateNewUserValid() {
		Mockito.when(roleService.checkAccessForUser("userId", ApplicationConstants.ROLE_ADD_NEW_USER)).thenReturn(true);
		userService.createNewUser("userId", new User());
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.UserServiceImpl#createNewUser(java.lang.String, com.turvo.abcbanking.model.User)}.
	 */
	@Test
	public final void testCreateNewUserRedundantUserId() {
		Mockito.when(roleService.checkAccessForUser("userId", ApplicationConstants.ROLE_ADD_NEW_USER)).thenReturn(true);
		Mockito.when(userRepository.exists("existingUserId")).thenReturn(true);
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_USER_ID_EXIST);
		User user = new User();
		user.setUserId("existingUserId");
		userService.createNewUser("userId", user);
	}
}
