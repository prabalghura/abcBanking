package com.turvo.abcbanking.utils;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.turvo.abcbanking.model.Customer;
import com.turvo.abcbanking.model.Service;

/**
 * Container class for composition of customer and service list object
 * 
 * @author Prabal Ghura
 *
 */
public class CustomerServiceContainer {

	@NotNull
	private Customer customer;
	
	@NotNull
	private List<Service> services;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}
}
