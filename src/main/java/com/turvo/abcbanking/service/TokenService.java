package com.turvo.abcbanking.service;

import java.util.List;

import com.turvo.abcbanking.model.Customer;
import com.turvo.abcbanking.model.Service;
import com.turvo.abcbanking.model.Token;

/**
 * Service contract/interface for Token operations
 * 
 * @author Prabal Ghura
 *
 */
public interface TokenService {

	/**
	 * To create a token for a customer and assigns it to a counter.
	 * 
	 * @param customer
	 * @param branchId
	 * @param list of services
	 * @return new Token instance
	 */
	public Token createToken(Customer customer, Long branchId, List<Service> services);
	
	/**
	 * To mark a token as completed 
	 * 
	 * @param executorId
	 * @param branchId
	 * @param counterNumber
	 */
	public void markTokenAsCompleted(String executorId, Long branchId, Integer tokenNumber);
	
	/**
	 * To mark a token as cancelled 
	 * 
	 * @param executorId
	 * @param branchId
	 * @param counterNumber
	 */
	public void markTokenAsCancelled(String executorId, Long branchId, Integer tokenNumber);
}
