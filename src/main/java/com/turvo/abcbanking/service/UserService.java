package com.turvo.abcbanking.service;

import java.util.List;

import com.turvo.abcbanking.model.User;

/**
 * Service contract/interface for User operations
 * 
 * @author Prabal Ghura
 *
 */
public interface UserService {
	
	/**
	 * Gets all the users defined in the system
	 * 
	 * @return list of users
	 */
	public List<User> getAllUsers();
	
	/**
	 * Gets a specific user by id.
	 * 
	 * @param userId
	 * @return user instance if exists null otherwise
	 */
	public User getUser(String userId);
	
	/**
	 * Creates a new user in the system
	 * 
	 * @param creatorId
	 * @param user
	 * @return new user instance
	 */
	public User createNewUser(String creatorId, User user);
}
