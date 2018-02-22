package com.turvo.abcbanking.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.service.RoleService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Abstract class to provide common functionalities across all services
 * 
 * @author Prabal Ghura
 *
 */
public abstract class BaseServiceImpl {
	
	@Autowired
	RoleService roleService;
	
	/**
	 * to throw an exception if user doesn't have access to perform an operation
	 * 
	 * @param userId
	 * @param role
	 */
	protected void checkAccess(String userId, String role) {
		if(!roleService.checkAccessForUser(userId, role))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_ACCESS_DENIED);
	}
}
