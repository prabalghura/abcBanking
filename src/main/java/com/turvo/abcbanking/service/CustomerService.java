package com.turvo.abcbanking.service;

import com.turvo.abcbanking.model.Customer;

/**
 * Service contract/interface for Customer operations
 * 
 * @author Prabal Ghura
 *
 */
public interface CustomerService {

	/**
	 * For getting a customer instance
	 * 
	 * @param accountNumber
	 * @return customer instance if exists
	 */
	public Customer getCustomer(Long accountNumber);
	
	/**
	 * For creating a new customer
	 * 
	 * @param customer
	 * @return new customer instance
	 */
	public Customer createNewCustomer(Customer customer);
}
