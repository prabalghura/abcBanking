package com.turvo.abcbanking.model;

/**
 * Enum representing different states of a token workflow step
 * 
 * @author Prabal Ghura
 *
 */
public enum TokenWorklowStatus {
	
	/**
	 * Token has completed this workflow step
	 */
	COMPLETED,
	
	/**
	 * Only one step in a token workflow can have this status at any time, 
	 * represents that token is currently being serviced or queued to be serviced for this step
	 */
	ASSIGNED,
	
	/**
	 * Initial status for all workflow step
	 */
	PENDING
}
