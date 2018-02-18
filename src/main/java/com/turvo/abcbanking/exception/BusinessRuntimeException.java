package com.turvo.abcbanking.exception;

/**
 * Custom Exception class for throwing Runtime Business Exception
 * 
 * @author Prabal Ghura
 *
 */
public class BusinessRuntimeException extends RuntimeException{

	private static final long serialVersionUID = 6138290865512671897L;

	/**
	 * Constructor
	 * 
	 * @param message
	 */
	public BusinessRuntimeException(String message) {
		super(message);
	}
}
