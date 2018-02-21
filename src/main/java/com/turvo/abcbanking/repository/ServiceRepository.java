package com.turvo.abcbanking.repository;

import org.springframework.stereotype.Repository;

import com.turvo.abcbanking.model.Service;

/**
 * Standard repository class for Service operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
public interface ServiceRepository extends BaseRepository<Service, Long>, ServiceRepositoryCustom {
}
