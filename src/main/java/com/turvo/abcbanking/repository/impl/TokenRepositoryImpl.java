package com.turvo.abcbanking.repository.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.turvo.abcbanking.model.CustomerType;
import com.turvo.abcbanking.model.Token;
import com.turvo.abcbanking.model.TokenStatus;
import com.turvo.abcbanking.model.TokenWorkflow;
import com.turvo.abcbanking.model.TokenWorklowStatus;
import com.turvo.abcbanking.repository.TokenRepositoryCustom;
import com.turvo.abcbanking.utils.CustomQueries;

/**
 * Custom repository implementation for Token operations
 * 
 * @author Prabal Ghura
 *
 */
@Repository
@Transactional(readOnly = true)
public class TokenRepositoryImpl implements TokenRepositoryCustom{

	@PersistenceContext
    EntityManager entityManager;

	@Override
	public List<Token> getTokensForCounter(Long counterId) {
		Query query = entityManager.createNativeQuery(CustomQueries.TOKEN_FOR_COUNTER);
        query.setParameter(1, counterId);
        List<Object[]> resultList =  query.getResultList();
        Integer previousTokenId = null;
        Token token = null;
        
        List<Token> tokens = new ArrayList<>();
        List<TokenWorkflow> steps = new ArrayList<>();
        
        for(Object[] record: resultList) {
        	Integer tokenId = (Integer) record[0];
        	Integer accountNumber = (Integer) record[1];
        	Integer displayId = (Integer) record[2];
        	String status = (String) record[3];
        	Date tokenCreationDate = (Date) record[4];
        	String servicingType = (String) record[13];
        	Integer workflowId = (Integer) record[5];
        	Integer tokenId1 = (Integer) record[6];
        	Integer counterId1 = (Integer) record[7];
        	Integer stepId = (Integer) record[8];
        	String servedBy = (String) record[9];
        	String workFlowStatus = (String) record[10];
        	String comments = (String) record[11];
        	Date servedDate = (Date) record[12];
        	if(!tokenId.equals(previousTokenId)) {
        		if(!Objects.isNull(token)) {
        			token.setSteps(steps);
        			steps = new ArrayList<>();
        			tokens.add(token);
        		}
        		token = new Token();
        		token.setId(tokenId.longValue());
        		token.setAccountNumber(accountNumber.longValue());
        		token.setNumber(displayId);
        		token.setStatus(TokenStatus.valueOf(status));
        		token.setCreatedDate(tokenCreationDate);
        		token.setType(CustomerType.valueOf(servicingType));
        	}
        	TokenWorkflow step = new TokenWorkflow();
    		step.setId(workflowId.longValue());
    		step.setTokenId(tokenId1.longValue());
    		step.setCounterId(counterId1.longValue());
    		step.setStepId(stepId.longValue());
    		step.setServedBy(servedBy);
    		step.setStatus(TokenWorklowStatus.valueOf(workFlowStatus));
    		step.setComments(comments);
    		step.setServedDate(servedDate);
    		steps.add(step);
    		previousTokenId = tokenId;
        }
        if(!Objects.isNull(token)) {
        	token.setSteps(steps);
			tokens.add(token);
		}
        return tokens;
	}
}
