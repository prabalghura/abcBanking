package com.turvo.abcbanking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turvo.abcbanking.service.TokenService;

/**
 * Controller class for Counter related operations
 * 
 * @author Prabal Ghura
 *
 */
@RestController
@RequestMapping("/api")
public class TokenController {
	
	@Autowired
	TokenService tokenService;
	
	/**
	 * For marking a token as completed
	 * 
	 * @param executorId
	 * @param branchId
	 * @param tokenNumber
	 * @return success if operation is successful
	 */
	@PostMapping("/branches/{id}/token/{tokenId}/complete")
	public String markTokenAsCompleted(@RequestHeader("userId") String executorId, @PathVariable(value = "id") Long branchId, 
			@PathVariable(value = "tokenId") Integer tokenNumber) {
		tokenService.markTokenAsCompleted(executorId, branchId, tokenNumber);
		return "SUCCESS";
	}
	
	/**
	 * For marking a token as cancelled
	 * 
	 * @param executorId
	 * @param branchId
	 * @param tokenNumber
	 * @return success if operation is successful
	 */
	@PostMapping("/branches/{id}/token/{tokenId}/cancel")
	public String markTokenAsCancelled(@RequestHeader("userId") String executorId, @PathVariable(value = "id") Long branchId, 
			@PathVariable(value = "tokenId") Integer tokenNumber) {
		tokenService.markTokenAsCancelled(executorId, branchId, tokenNumber);
		return "SUCCESS";
	}
}
