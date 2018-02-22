package com.turvo.abcbanking.repository;

import java.util.List;

import com.turvo.abcbanking.model.CustomerType;
import com.turvo.abcbanking.model.Service;

/**
 * Custom repository class for Service operations
 * 
 * @author Prabal Ghura
 *
 */
public interface ServiceRepositoryCustom {
	
	/**
	 * To get all the services a branch serves for a customer Type
	 * 
	 * @param branchId
	 * @param type
	 * @return list of services
	 */
	public List<Service> getServicesForBranch(Long branchId, CustomerType type);
}
