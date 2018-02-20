package com.turvo.abcbanking.utils;

/**
 * Util class to store Custom queries for the repositories.
 * 
 * @author Prabal Ghura
 *
 */
public class CustomQueries {
	
	private CustomQueries() {
		super();
	}

	public static final String USER_ROLE_CHECK_ACCESS = "select count(u) from User u, Role r, UserXRole uxr where u.userId = uxr.userId "
			+ "and r.id = uxr.roleId and u.userId = ?1 and r.name = ?2";
	
	public static final String USER_ROLES = "select r from Role r, UserXRole uxr where r.id = uxr.roleId "
			+ "and uxr.userId = ?1";
	
	public static final String COUNTER_MAX_NUMBER = "select coalesce(max(c.number), 0) from Counter c where c.branchId = ?1";
	
	public static final String WORKFLOW_FOR_SERVICE = "select step from ServiceXServiceStep sxs, ServiceStep step "
			+ "where sxs.stepId=step.id and sxs.serviceId=?1 order by sxs.order";
}
