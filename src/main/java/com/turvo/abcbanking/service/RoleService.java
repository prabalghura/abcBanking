package com.turvo.abcbanking.service;

import java.util.List;

import com.turvo.abcbanking.model.Role;

/**
 * Service contract/interface for Role operations
 * 
 * @author Prabal Ghura
 *
 */
public interface RoleService {

	/**
	 * Checks if user has been assigned a role
	 * 
	 * @param userId
	 * @param role
	 * @return true if access is present false otherwise
	 */
	public Boolean checkAccessForUser(String userId, String role);
	
	/**
	 * Gets all the roles assigned to a user
	 * 
	 * @param userId
	 * @return list of roles
	 */
	public List<Role> getUserRoles(String userId);
	
	/**
	 * Assigns a list of roles to the user
	 * 
	 * @param assignerId
	 * @param userId
	 * @param roles
	 * @return list of current roles user have
	 */
	public List<Role> assignRolesToUser(String assignerId, String userId, List<Role> roles);
	
	/**
	 * Gets all the roles defined in the system.
	 * 
	 * @return list of roles
	 */
	public List<Role> getAllRoles();
}
