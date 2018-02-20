package com.turvo.abcbanking.repository;

import org.springframework.stereotype.Repository;

import com.turvo.abcbanking.model.Branch;

/**
 * Standard repository class for Branch operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
public interface BranchRepository extends BaseRepository<Branch, Long>{
}
