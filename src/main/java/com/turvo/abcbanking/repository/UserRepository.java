package com.turvo.abcbanking.repository;

import org.springframework.stereotype.Repository;

import com.turvo.abcbanking.model.User;

/**
 * Standard repository class for User operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
public interface UserRepository extends BaseRepository<User, String>{
}
