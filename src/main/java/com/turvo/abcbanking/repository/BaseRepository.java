package com.turvo.abcbanking.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, I extends Serializable> extends CrudRepository<T, I>{
	
	public List<T> findAll();
	
	public void flush();
	
	public <S extends T> S saveAndFlush(S entity);
}
