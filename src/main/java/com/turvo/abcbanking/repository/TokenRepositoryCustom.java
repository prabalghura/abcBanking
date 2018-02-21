package com.turvo.abcbanking.repository;

import java.util.List;

import com.turvo.abcbanking.model.Token;

/**
 * Custom repository class for Token operations
 * 
 * @author Prabal Ghura
 *
 */
public interface TokenRepositoryCustom {

	/**
	 * To get all the token a counter currently has to serve
	 * 
	 * @param counterId
	 * @return list of tokens
	 */
	public List<Token> getTokensForCounter(Long counterId);
}
