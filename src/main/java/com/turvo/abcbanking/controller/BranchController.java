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

import com.turvo.abcbanking.model.Branch;
import com.turvo.abcbanking.service.BranchService;

/**
 * Controller class for User/Role related operations
 * 
 * @author Prabal Ghura
 *
 */
@RestController
@RequestMapping("/api")
public class BranchController {

	@Autowired
	BranchService branchService;
	
	/**
	 * For getting all the branches registered in the system
	 * 
	 * @return list of all branches
	 */
	@RequestMapping("/branches")
	public List<Branch> getBranches() {
		return branchService.getAllBranches();
	}
	
	/**
	 * For getting a specific branch
	 * 
	 * @param branchId
	 * @return branch if existing null otherwise
	 */
	@RequestMapping("/branches/{id}")
	public Branch getBranch(@PathVariable(value = "id") Long branchId) {
		return branchService.getBranch(branchId);
	}
	
	/**
	 * For creating a new branch
	 * 
	 * @param creatorId
	 * @param branch
	 * @return new branch instance
	 */
	@PostMapping("/branches")
	public Branch createBranch(@RequestHeader("userId") String creatorId, @Valid @RequestBody Branch branch) {
	    return branchService.createNewBranch(creatorId, branch);
	}
	
	/**
	 * For assigning manager to a branch
	 * 
	 * @param assignerId
	 * @param branchId
	 * @param managerId
	 * @return new branch instance
	 */
	@PostMapping("/branches/{id}/manager/{managerId}")
	public Branch assignManagerToBranch(@RequestHeader("userId") String assignerId, 
			@PathVariable(value = "id") Long branchId, @PathVariable(value = "managerId") String managerId) {
	    return branchService.assignManager(assignerId, branchId, managerId);
	}
}
