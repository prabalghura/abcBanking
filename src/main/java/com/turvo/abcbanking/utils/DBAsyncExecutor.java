package com.turvo.abcbanking.utils;

import java.util.Arrays;
import java.util.List;

import com.turvo.abcbanking.repository.BaseRepository;

/**
 * Util class for asynchronously saving Token into DB
 * 
 * @author Prabal Ghura
 *
 */
public class DBAsyncExecutor<T, R extends BaseRepository<T, ?>> implements Runnable {

	private List<T> t;
	
	private R repository;

	/**
	 * @param t
	 * @param repository
	 */
	public DBAsyncExecutor(List<T> t, R repository) {
		super();
		this.t = t;
		this.repository = repository;
	}
	
	public DBAsyncExecutor(T t, R repository) {
		super();
		this.t = Arrays.asList(t);
		this.repository = repository;
	}

	@Override
	public void run() {
		repository.save(t);
		repository.flush();
	}
}
