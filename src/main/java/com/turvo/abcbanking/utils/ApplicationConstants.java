package com.turvo.abcbanking.utils;

/**
 * Utils class to provide application vide constants.
 * 
 * @author Prabal Ghura
 *
 */
public class ApplicationConstants {

	private ApplicationConstants() {
		super();
	}
	
	// Error Messages
	public static final String ERR_BRANCH_NOT_EXIST = "Branch does not exist";
	public static final String ERR_OPERATOR_NOT_EXIST = "Operator does not exist";
	public static final String ERR_MANAGER_NOT_EXIST = "Manager does not exist";
	public static final String ERR_COUNTER_NOT_EXIST = "Counter does not exist";
	public static final String ERR_SERVICE_NOT_EXIST = "Service does not exist";
	public static final String ERR_SERVICE_STEP_NOT_EXIST = "Service Step does not exist";
	public static final String ERR_ACCESS_DENIED = "Access Denied";
	public static final String ERR_NEED_AUTHORIZATION = "Operation needs authorization";
	public static final String ERR_USER_NOT_EXIST = "User does not exist";
	public static final String ERR_USER_ID_EXIST = "UserId already exists";
	public static final String ERR_INVALID_SERVICE_NAME = "Service name must not be empty";
	public static final String ERR_INVALID_SERVICE_STEP_NAME = "Service step name must not be empty";
	public static final String ERR_INVALID_SERVICE_STEP_ID = "Service step id must not be null";
	public static final String ERR_EMPTY_COUNTER_QUEUE = "Counter queue is empty";
	public static final String ERR_EMPTY_SERVICE_TOKEN = "Service list must not be empty";
	public static final String ERR_CUSTOMER_NOT_EXIST = "Customer does not exists";
	public static final String ERR_TOKEN_NOT_EXIST = "Token does not exists";
	
	// Roles
	public static final String ROLE_ASSIGN_ROLES = "ASSIGN_ROLES";
	public static final String ROLE_ADD_NEW_BRANCH = "ADD_NEW_BRANCH";
	public static final String ROLE_ADD_NEW_USER = "ADD_NEW_USER";
	public static final String ROLE_DEFINE_SERVICE = "DEFINE_SERVICE";
}
