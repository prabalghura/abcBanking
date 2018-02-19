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
	
	public static final String ERR_BRANCH_NOT_EXIST = "Branch does not exist";
	public static final String ERR_OPERATOR_NOT_EXIST = "Operator does not exist";
	public static final String ERR_MANAGER_NOT_EXIST = "Manager does not exist";
	public static final String ERR_COUNTER_NOT_EXIST = "Counter does not exist";
	public static final String ERR_ACCESS_DENIED = "Access Denied";
	public static final String ERR_NEED_AUTHORIZATION = "Operation needs authorization";
	public static final String ERR_USER_NOT_EXIST = "User doesn't exist.";
	public static final String ERR_USER_ID_EXIST = "UserId already exists";
	
	public static final String ROLE_ASSIGN_ROLES = "ASSIGN_ROLES";
	public static final String ROLE_ADD_NEW_BRANCH = "ADD_NEW_BRANCH";
	public static final String ROLE_ADD_NEW_USER = "ADD_NEW_USER";

}
