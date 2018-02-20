package com.turvo.abcbanking.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.turvo.abcbanking.model.UserXRole;

/**
 * Standard repository class for UserXRole operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
public interface UserXRoleRepository extends BaseRepository<UserXRole, Long>{
	
	/**
	 * Find role ids assigned to user
	 * 
	 * @param userId
	 * @return list of role ids
	 */
	List<UserXRole> findByUserId(String userId);
}
