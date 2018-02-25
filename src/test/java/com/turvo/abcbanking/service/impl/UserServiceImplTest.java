package com.turvo.abcbanking.service.impl;

import java.util.ArrayList;
import java.util.List;
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
	 * Stubbing all dependencies at one place
	 */
	@Before
	public final void stubDependencies() {
		User user = new User();
		user.setUserId("userId");
		user.setName("User");
		List<User> userList = new ArrayList<>();
		userList.add(user);
		Mockito.when(userRepository.findAll()).thenReturn(userList);
		Mockito.when(userRepository.findOne("userId")).thenReturn(user);
		Mockito.when(roleService.checkAccessForUser("userIdWithAccess", ApplicationConstants.ROLE_ADD_NEW_USER)).thenReturn(true);
		Mockito.when(userRepository.exists("existingUserId")).thenReturn(true);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.UserServiceImpl#getAllUsers()}.
	 */
	@Test
	public final void testGetAllUsers() {
		Assert.assertFalse("User list should not be null", Objects.isNull(userService.getAllUsers()));
		Assert.assertFalse("User list should contain stubbed instance", Objects.isNull(userService.getAllUsers().get(0)));
		Assert.assertTrue("User instance should be same as stubbed instance", userService.getAllUsers().get(0)
				.getName().equalsIgnoreCase("User"));
		Assert.assertTrue("User instance should be same as stubbed instance", userService.getAllUsers().get(0)
				.getUserId().equalsIgnoreCase("userId"));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.UserServiceImpl#getUser(java.lang.String)}.
	 */
	@Test
	public final void testGetUser() {
		Assert.assertFalse("User should not be null", Objects.isNull(userService.getUser("userId")));
		Assert.assertTrue("User instance should be same as stubbed instance", userService.getUser("userId").getName().
				equalsIgnoreCase("User"));
	}

	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.UserServiceImpl#createNewUser(java.lang.String, com.turvo.abcbanking.model.User)}.
	 */
	@Test
	public final void testCreateNewUserWithoutAccess() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_ACCESS_DENIED);
		User user = new User();
		user.setUserId("newUserId");
		userService.createNewUser("userIdWithouAccess", user);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.UserServiceImpl#createNewUser(java.lang.String, com.turvo.abcbanking.model.User)}.
	 */
	@Test
	public final void testCreateNewUserRedundantUserId() {
		exception.expect(BusinessRuntimeException.class);
		exception.expectMessage(ApplicationConstants.ERR_USER_ID_EXIST);
		User user = new User();
		user.setUserId("existingUserId");
		userService.createNewUser("userIdWithAccess", user);
	}
	
	/**
	 * Test method for {@link com.turvo.abcbanking.service.impl.UserServiceImpl#createNewUser(java.lang.String, com.turvo.abcbanking.model.User)}.
	 */
	@Test
	public final void testCreateNewUserValid() {
		User user = new User();
		user.setUserId("newUserId");
		userService.createNewUser("userIdWithAccess", user);
	}
}
