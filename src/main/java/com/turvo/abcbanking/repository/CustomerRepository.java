package com.turvo.abcbanking.repository;

import org.springframework.stereotype.Repository;

import com.turvo.abcbanking.model.Customer;

/**
 * Standard repository class for Customer operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
public interface CustomerRepository extends BaseRepository<Customer, Long>{
}
