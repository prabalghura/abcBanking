package com.turvo.abcbanking.model;

/**
 * Enum representing different states of a token
 * 
 * @author Prabal Ghura
 *
 */
public enum TokenStatus {
	
	/**
	 * Token is in the serving queue
	 */
	PENDING, 
	
	/**
	 * Marking status by operator or branch manager effectively removing token from current counter service queue
	 */
	COMPLETED, 
	CANCELLED
}
