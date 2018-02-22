package com.turvo.abcbanking.repository;

import org.springframework.stereotype.Repository;

import com.turvo.abcbanking.model.TokenWorkflow;

/**
 * Standard repository class for Token Workflow operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
public interface TokenWorkflowRepository extends BaseRepository<TokenWorkflow, Long> {
}
