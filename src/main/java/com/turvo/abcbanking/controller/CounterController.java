package com.turvo.abcbanking.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turvo.abcbanking.model.Counter;
import com.turvo.abcbanking.service.CounterService;

/**
 * Controller class for Counter related operations
 * 
 * @author Prabal Ghura
 *
 */
@RestController
@RequestMapping("/api")
public class CounterController {

	@Autowired
	CounterService counterService;
	
	/**
	 * For getting all counters in a specific branch
	 * 
	 * @param branchId
	 * @return list of counters
	 */
	@RequestMapping("/branches/{id}/counters")
	public List<Counter> getBranchCounters(@PathVariable(value = "id") Long branchId) {
		return counterService.getAllBranchCounters(branchId);
	}
	
	/**
	 * For creating a new counter in a branch
	 * 
	 * @param creatorId
	 * @param branchId
	 * @param counter
	 * @return new counter instance
	 */
	@PostMapping("/branches/{id}/counters")
	public Counter createCounter(@RequestHeader("userId") String creatorId, @PathVariable(value = "id") Long branchId, 
			@Valid @RequestBody Counter counter) {
	    return counterService.createNewCounter(creatorId, branchId, counter);
	}
	
	/**
	 * Assigns a counter an operator
	 * 
	 * @param assignerId
	 * @param branchId
	 * @param counterNumber
	 * @param operatorId
	 * @return updated counter instance
	 */
	@PostMapping("/branches/{id}/counters/{counterId}/operator/{operatorId}")
	public Counter assignOperatorToCounter(@RequestHeader("userId") String assignerId, @PathVariable(value = "id") Long branchId, 
			@PathVariable(value = "counterId") Integer counterNumber, @PathVariable(value = "operatorId") String operatorId) {
		return counterService.assignOperator(assignerId, branchId, counterNumber, operatorId);
	}
}
