package com.turvo.abcbanking.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turvo.abcbanking.model.Role;
import com.turvo.abcbanking.model.User;
import com.turvo.abcbanking.service.RoleService;
import com.turvo.abcbanking.service.UserService;

/**
 * Controller class for User/Role related operations
 * 
 * @author Prabal Ghura
 *
 */
@RestController
@RequestMapping("/api")
public class UserRoleController {

	@Autowired
	UserService userService;
	
	@Autowired
	RoleService roleService;
	
	/**
	 * For getting all the users registered in the system
	 * 
	 * @return list of all users
	 */
	@RequestMapping("/users")
	public List<User> getUsers() {
		return userService.getAllUsers();
	}
	
	/**
	 * For getting a specific user
	 * 
	 * @param userId
	 * @return user if existing
	 */
	@RequestMapping("/users/{id}")
	public User getUser(@PathVariable(value = "id") String userId) {
		return userService.getUser(userId);
	}
	
	/**
	 * For creating a new user
	 * 
	 * @param creatorId
	 * @param user
	 * @return new user instance
	 */
	@PostMapping("/users")
	public User createUser(@RequestHeader("userId") String creatorId, @Valid @RequestBody User user) {
	    return userService.createNewUser(creatorId, user);
	}
	
	/**
	 * For getting roles for a specific user
	 * 
	 * @param userId
	 * @return list of roles assigned to the user
	 */
	@RequestMapping("/users/{id}/roles")
	public List<Role> getUserRoles(@PathVariable(value = "id") String userId) {
		return roleService.getUserRoles(userId);
	}
	
	/**
	 * For assigning roles to a specific user
	 * 
	 * @param assignerId
	 * @param userId
	 * @param roles
	 * @return list of all the roles user now have
	 */
	@PostMapping("/users/{id}/roles")
	public List<Role> assignRolesToUser(@RequestHeader("userId") String assignerId, 
			@PathVariable(value = "id") String userId, @Valid @RequestBody List<Role> roles) {
		return roleService.assignRolesToUser(assignerId, userId, roles);
	}
	
	/**
	 * Gets all roles defined in the system
	 * 
	 * @return list of all the roles
	 */
	@RequestMapping("/roles")
	public List<Role> getRoles() {
		return roleService.getAllRoles();
	}
}
