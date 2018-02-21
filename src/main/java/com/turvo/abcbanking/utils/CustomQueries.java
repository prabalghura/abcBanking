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
	
	public static final String STEPS_FOR_COUNTER = "select step from CounterXServiceStep cxs, ServiceStep step "
			+ "where cxs.stepId=step.id and cxs.counterId=?1";
	
	public static final String SERVICE_FOR_BRANCH = "select s.ID, s.NAME, step.ID STEP_ID, step.NAME STEP_NAME from SERVICEXSERVICE_STEP sxs1 "
			+ "inner join SERVICE s on sxs1.SERVICE_ID = s.ID inner join SERVICE_STEP step on step.ID = sxs1.STEP_ID left outer join "
			+ "(select sxs.SERVICE_ID SERVICE_ID from SERVICEXSERVICE_STEP sxs left outer join (SELECT cxs.STEP_ID STEP_ID FROM COUNTER c, "
			+ "COUNTERXSERVICE_STEP cxs where c.ID = cxs.COUNTER_ID AND c.BRANCH_ID = ? AND c.TYPE = ? GROUP BY cxs.STEP_ID) bxs "
			+ "on bxs.STEP_ID = sxs.STEP_ID where bxs.STEP_ID is null) bxns on s.ID = bxns.SERVICE_ID where bxns.SERVICE_ID is null "
			+ "order by s.ID, sxs1.WORKFLOW_ORDER";
}
