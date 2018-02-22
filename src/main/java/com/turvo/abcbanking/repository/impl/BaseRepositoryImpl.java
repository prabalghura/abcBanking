package com.turvo.abcbanking.repository.impl;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.turvo.abcbanking.repository.BaseRepository;

/**
 * Base Repository Implementation
 * 
 * Currently extending SimpleJpaRepository can be changed if underlying DB changes.
 * 
 * @author Prabal Ghura
 *
 * @param <T> Entity Type
 * @param <I> Entity unique identifier (primary as we have in SQL databases)
 */
public class BaseRepositoryImpl <T, I extends Serializable> 
extends SimpleJpaRepository<T, I> implements BaseRepository<T, I>{
	
	protected final EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }
}
