package com.turvo.abcbanking.repository;

import org.springframework.stereotype.Repository;

import com.turvo.abcbanking.model.Token;

/**
 * Standard repository class for Token operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
public interface TokenRepository extends BaseRepository<Token, Long>{
}
