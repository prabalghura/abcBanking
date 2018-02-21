package com.turvo.abcbanking.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turvo.abcbanking.model.Customer;
import com.turvo.abcbanking.model.Service;
import com.turvo.abcbanking.model.Token;
import com.turvo.abcbanking.service.CustomerService;
import com.turvo.abcbanking.service.TokenService;
import com.turvo.abcbanking.utils.CustomerServiceContainer;

/**
 * Controller class for Customer related operations
 * 
 * @author Prabal Ghura
 *
 */
@RestController
@RequestMapping("/api")
public class CustomerController {
	
	@Autowired
	TokenService tokenService;
	
	@Autowired
	CustomerService customerService;
	
	/**
	 * For creating token for an existing customer
	 * 
	 * @param branchId
	 * @param accountNumber
	 * @param services
	 * @return token instance
	 */
	@PostMapping("/branches/{id}/customer/{accountNumber}/token")
	public Token getTokenForExistingCustomer(@PathVariable(value = "id") Long branchId, 
			@PathVariable(value = "accountNumber") Long accountNumber, @Valid @RequestBody List<Service> services) {
		Customer customer = customerService.getCustomer(accountNumber);
		return tokenService.createToken(customer, branchId, services);
	}
	
	/**
	 * For creating token for an existing customer
	 * 
	 * @param branchId
	 * @param Customer & Service Container
	 * @return token instance
	 */
	@PostMapping("/branches/{id}/token")
	public Token getTokenForNewCustomer(@PathVariable(value = "id") Long branchId, 
			@Valid @RequestBody CustomerServiceContainer container) {
		Customer customer = customerService.createNewCustomer(container.getCustomer());
		return tokenService.createToken(customer, branchId, container.getServices());
	}
}
