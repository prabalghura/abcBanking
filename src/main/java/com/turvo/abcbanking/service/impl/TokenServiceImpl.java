package com.turvo.abcbanking.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.model.Branch;
import com.turvo.abcbanking.model.Counter;
import com.turvo.abcbanking.model.Customer;
import com.turvo.abcbanking.model.Service;
import com.turvo.abcbanking.model.ServiceStep;
import com.turvo.abcbanking.model.Token;
import com.turvo.abcbanking.model.TokenStatus;
import com.turvo.abcbanking.model.TokenWorkflow;
import com.turvo.abcbanking.model.TokenWorklowStatus;
import com.turvo.abcbanking.repository.TokenRepository;
import com.turvo.abcbanking.repository.TokenWorkflowRepository;
import com.turvo.abcbanking.service.BranchService;
import com.turvo.abcbanking.service.TokenService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Service implementation for Counter operations
 * 
 * @author Prabal Ghura
 *
 */
@org.springframework.stereotype.Service("tokenService")
public class TokenServiceImpl extends BaseServiceImpl implements TokenService {

	@Autowired
	BranchService branchService;
	
	@Autowired
	TokenRepository tokenRepository;
	
	@Autowired
	TokenWorkflowRepository tokenWorkflowRepository;
	
	@Override
	@Transactional(readOnly = false)
	public Token createToken(Customer customer, Long branchId, List<Service> services) {
		if(Objects.isNull(services) || services.isEmpty())
			throw new BusinessRuntimeException(ApplicationConstants.ERR_EMPTY_SERVICE_TOKEN);
		List<ServiceStep> steps = new ArrayList<>();
		services.forEach(service -> steps.addAll(service.getSteps()));
		
		Branch branch = branchService.getBranch(branchId);
		
		Token token = new Token();
		token.setAccountNumber(customer.getAccountNumber());
		token.setNumber(branch.getTokenNumber());
		token.setStatus(TokenStatus.PENDING);
		
		/**
		 * Bottleneck #1
		 * this is the bottleneck need to wait everytime this operation executes 
		 * rest all high volume db operations can be passed to a queue but this can't be
		 * because I need token Id for further operations
		 */
		token = tokenRepository.save(token);
		
		List<TokenWorkflow> workflowSteps = new ArrayList<>();
		for(ServiceStep step: steps) {
			TokenWorkflow workflowStep = new TokenWorkflow();
			
			workflowStep.setStatus(TokenWorklowStatus.PENDING);
			workflowStep.setStepId(step.getId());
			workflowStep.setTokenId(token.getId());
			
			workflowSteps.add(workflowStep);
		}
		Counter firstCounter = branchService.getBestCounter(branchId, customer.getType(), steps.get(0).getId());
		
		TokenWorkflow workflowStep = workflowSteps.get(0);
		
		workflowStep.setStatus(TokenWorklowStatus.ASSIGNED);
		workflowStep.setCounterId(firstCounter.getId());
		
		workflowSteps.set(0, workflowStep);
		
		
		/**
		 * Bottleneck #2
		 * 
		 */
		workflowSteps = tokenWorkflowRepository.save(workflowSteps);
		tokenWorkflowRepository.flush();
		
		token.setType(customer.getType());
		token.setSteps(workflowSteps);
		
		firstCounter.addToken(token);
		
		branchService.updateCounter(firstCounter);
		
		return token;
	}
}