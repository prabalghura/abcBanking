package com.turvo.abcbanking.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Generic base repository contract/interface to cater DB operations for multiple system entities
 * 
 * This contract inserts a layer of abstraction between database used and system entity repositories
 * 
 * Current solution is based on SQL database that's why extended CrudRepository for basic operation currently.
 * 
 * @author Prabal Ghura
 *
 * @param <T> Entity Type
 * @param <I> Entity unique identifier (primary as we have in SQL databases)
 */
@NoRepositoryBean
public interface BaseRepository<T, I extends Serializable> extends CrudRepository<T, I>{
	
	public List<T> findAll();
	
	public void flush();
	
	public <S extends T> S saveAndFlush(S entity);
	
	public <S extends T> List<S> save(Iterable<S> entities);
}
