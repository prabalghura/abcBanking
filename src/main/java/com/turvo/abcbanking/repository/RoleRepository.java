package com.turvo.abcbanking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.turvo.abcbanking.model.Role;
import com.turvo.abcbanking.utils.CustomQueries;

/**
 * Standard repository class for Role operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
public interface RoleRepository extends BaseRepository<Role, Integer> {
	
	/**
	 * Checks whether user holds the specifed role
	 * 
	 * @param userId
	 * @param role
	 * @return 1 if user holds the role 0 otherwise
	 */
	@Query(CustomQueries.USER_ROLE_CHECK_ACCESS)
	Integer checkAccess(String userId, String role);
	
	/**
	 * Get all the roles assigned to a user
	 * 
	 * @param userId
	 * @return list of roles
	 */
	@Query(CustomQueries.USER_ROLES)
	List<Role> getRolesForUser(String userId);
	
	/**
	 * Find roles by name (in clause)
	 * 
	 * @param names
	 * @return
	 */
	List<Role> findByNameIn(List<String> names);
}
