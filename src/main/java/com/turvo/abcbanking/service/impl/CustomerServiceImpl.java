package com.turvo.abcbanking.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.model.Customer;
import com.turvo.abcbanking.repository.CustomerRepository;
import com.turvo.abcbanking.service.CustomerService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Service implementation for Customer operations
 * 
 * @author Prabal Ghura
 *
 */
@Service("customerService")
public class CustomerServiceImpl extends BaseServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository;
	
	/**
	 * Customer instance is fetched from DB and returned
	 * Exception is thrown if found none.
	 */
	@Override
	public Customer getCustomer(Long accountNumber) {
		Customer customer = customerRepository.findOne(accountNumber);
		if(Objects.isNull(customer))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_CUSTOMER_NOT_EXIST);
		return customer;
	}

	/**
	 * Passed Customer instance is persisted in DB and returned
	 */
	@Override
	public Customer createNewCustomer(Customer customer) {
		return customerRepository.save(customer);
	}
}
