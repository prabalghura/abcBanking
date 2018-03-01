package com.turvo.abcbanking.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.model.Branch;
import com.turvo.abcbanking.model.Counter;
import com.turvo.abcbanking.model.Customer;
import com.turvo.abcbanking.model.CustomerType;
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
	
	/**
	 * To fetch full fledged services with steps serviced by branch
	 * 
	 * @param type
	 * @param branchId
	 * @param services
	 * @return
	 */
	private List<Service> getBranchServices(CustomerType type, Long branchId, List<Service> services) {
		List<Service> parentServices;
		Set<Long> passedServiceIds = services.stream().map(Service::getId).collect(Collectors.toSet());
		
		if(passedServiceIds.size() != services.size())
			throw new BusinessRuntimeException(ApplicationConstants.ERR_TOKEN_DUPLICATE_SERVICE);
		
		if(type == CustomerType.REGULAR)
			parentServices = getBranch(branchId).getRegularServices();
		else
			parentServices = getBranch(branchId).getPremiumServices();
		
		List<Service> resultList =  parentServices.stream()
				.filter(service -> passedServiceIds.contains(service.getId()))
				.collect(Collectors.toList());
		
		if(resultList.size() != services.size())
			throw new BusinessRuntimeException(ApplicationConstants.ERR_BRANCH_INVALID_SERVICE);
		
		return resultList;
	}
	
	/**
	 * This is 2 of 3 frequent operations in entire application which involves DB update
	 * But because of the way counter queues are maintained in application we need to wait for DB update to return result 
	 * therefore both DB updates are made synchronously
	 * 
	 * Services are checked for being empty
	 * Services are broken down into steps in an orderly fashion
	 * Pending Token is made with display number fetched from branch instance's token generator
	 * Token is saved synchronously in DB
	 * 
	 * Workflow steps are made based on services requested
	 * First step is assigned to best counter in branch and steps are saved in DB synchronously
	 * counter in branch cache is updated
	 * 
	 * And token is returned
	 */
	@Override
	@Transactional(readOnly = false)
	public Token createToken(Customer customer, Long branchId, List<Service> services) {
		List<ServiceStep> steps = new ArrayList<>();
		getBranchServices(customer.getType(), branchId, services)
			.forEach(service -> steps.addAll(service.getSteps()));
		
		Branch branch = branchService.getBranch(branchId);
		
		Token token = new Token();
		token.setAccountNumber(customer.getAccountNumber());
		token.setNumber(branch.getTokenNumber());
		token.setStatus(TokenStatus.PENDING);
		
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
		
		workflowSteps = tokenWorkflowRepository.save(workflowSteps);
		tokenWorkflowRepository.flush();
		
		token.setType(customer.getType());
		token.setSteps(workflowSteps);
		
		firstCounter.addToken(token);
		
		branchService.updateCounter(firstCounter);
		
		return token;
	}
	
	@Override
	public void markTokenAsCompleted(String executorId, Long branchId, Integer tokenNumber) {
		markToken(executorId, branchId, tokenNumber, TokenStatus.COMPLETED);
	}

	@Override
	public void markTokenAsCancelled(String executorId, Long branchId, Integer tokenNumber) {
		markToken(executorId, branchId, tokenNumber, TokenStatus.CANCELLED);
	}
	
	/**
	 * This is 3 of 3 frequent operations in entire application which involves DB update
	 * 
	 * Branch's all counter queues are searched for token exception is thrown if none found.
	 * If token is found, access check is performed to check if operation executor is branch manager 
	 * or operator of the counter to which token is assigned, exception is thrown if both cases fail
	 * 
	 * Token is removed from counter queue and counter is updated in JVM cache
	 * token is marked with passed status CANCELLED/COMPLETED and asynchronous DB update is made
	 * 
	 * @param executorId
	 * @param branchId
	 * @param tokenNumber
	 * @param status
	 */
	private void markToken(String executorId, Long branchId, Integer tokenNumber, TokenStatus status) {
		Branch branch = getBranch(branchId);
		List<Counter> counters = branch.getCounters();
		for(Counter counter: counters) {
			Token token = counter.hasToken(tokenNumber);
			if(!Objects.isNull(token)) {
				if(!branch.getManagerId().equals(executorId) && !counter.getCurrentOperator().equals(executorId))
					throw new BusinessRuntimeException(ApplicationConstants.ERR_ACCESS_DENIED);
				token.setStatus(status);
				
				tokenRepository.saveAndFlush(token);
				branchService.updateCounter(counter.removeToken(token));
				return;
			}
		}
		throw new BusinessRuntimeException(ApplicationConstants.ERR_TOKEN_NOT_EXIST);
	}
	
	/**
	 * Branch is fetched from JVM cache against passed branchId, exception is thrown if no such branch exists in cache
	 * 
	 * @param branchId
	 * @return branch instance if existing
	 */
	private Branch getBranch(Long branchId) {
		Branch branch =  branchService.getBranch(branchId);
		if(Objects.isNull(branch))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		return branch;
	}
}