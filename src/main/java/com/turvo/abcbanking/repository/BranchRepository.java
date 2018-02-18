package com.turvo.abcbanking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.turvo.abcbanking.model.Branch;

/**
 * Standard repository class for Branch operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, Long>{
}
