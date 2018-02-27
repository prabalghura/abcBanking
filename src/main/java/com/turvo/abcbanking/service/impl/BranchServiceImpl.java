package com.turvo.abcbanking.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.model.Branch;
import com.turvo.abcbanking.model.Counter;
import com.turvo.abcbanking.model.CustomerType;
import com.turvo.abcbanking.model.Service;
import com.turvo.abcbanking.model.ServiceStep;
import com.turvo.abcbanking.repository.BranchRepository;
import com.turvo.abcbanking.repository.ServiceRepository;
import com.turvo.abcbanking.repository.TokenRepository;
import com.turvo.abcbanking.service.BranchService;
import com.turvo.abcbanking.service.CounterService;
import com.turvo.abcbanking.service.UserService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Service implementation for Branch operations
 * 
 * @author Prabal Ghura
 *
 */
@org.springframework.stereotype.Service("branchService")
public class BranchServiceImpl extends BaseServiceImpl implements BranchService {
	
	/**
	 * Central JVM cache
	 * 
	 * Because all autowired beans in Spring uses a singleton pattern
	 * wherever branch service is autowired the same instance will be available
	 * therefore maintaining a central cache of branches initially fetched from DB.
	 * Whatever be the changes will be published to DB possibly asynchronously and 
	 * immediately updated in JVM cache (least possible granular level) 
	 * in thread safe manner for fast access.
	 * 
	 */
	private ConcurrentHashMap<Long, Branch> branches = new ConcurrentHashMap<>();

	@Autowired
	BranchRepository branchRepository;
	
	@Autowired
	CounterService counterService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	ServiceRepository serviceRepository;
	
	@Autowired
	TokenRepository tokenRepository;
	
	/**
	 * If branch cache is empty then it is fetched from DB
	 * This task is ran post system build for initial heavy lifting as well
	 * 
	 * @return List of branches made by traversing the cache
	 */
	@Override
	public List<Branch> getAllBranches() {
		if(branches.size() == 0) {
			List<Branch> branches2 = getBranchesFromDB();
			branches2.forEach(branch -> branches.put(branch.getId(), branch));
		}
		List<Branch> branches1 = new ArrayList<>();
		for (Map.Entry<Long, Branch> entry : branches.entrySet()) { 
			branches1.add(entry.getValue());
		}
		return branches1;
	}
	
	/**
	 * id is passed as key to map and instance is returned
	 */
	@Override
	public Branch getBranch(Long id) {
		return branches.get(id);
	}

	/**
	 * Access check is performed
	 * Passed branch object is validated
	 * Branch is pushed to DB and instance is created in cache synchronously
	 */
	@Override
	@Transactional(readOnly = false)
	public Branch createNewBranch(String creatorId, Branch branch) {
		checkAccess(creatorId, ApplicationConstants.ROLE_ADD_NEW_BRANCH);
		if(Objects.isNull(userService.getUser(branch.getManagerId())))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_MANAGER_NOT_EXIST);
		branch.setLastModifiedBy(creatorId);
		branch = updateBranch(getBranchFull(branchRepository.saveAndFlush(branch)));
		return branch;
	}

	/**
	 * Access check is performed
	 * Inputs are system validated
	 * Branch is updated in DB and instance is updated in cache synchronously
	 */
	@Override
	@Transactional(readOnly = false)
	public Branch assignManager(String assignerId, Long branchId, String managerId) {
		checkAccess(assignerId, ApplicationConstants.ROLE_ADD_NEW_BRANCH);
		if(Objects.isNull(userService.getUser(managerId)))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_MANAGER_NOT_EXIST);
		Branch branch = getBranch(branchId);
		if(Objects.isNull(branch))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		branch.setManagerId(managerId);
		branch.setLastModifiedBy(assignerId);
		branch = updateBranch(getBranchFull(branchRepository.saveAndFlush(branch)));
		return branch;
	}
	
	/**
	 * Access check is performed for branch manager
	 * Single Branch instance is fetched from DB
	 */
	@Override
	public Branch updateBranch(String managerId, Long branchId) {
		Branch branch = branchRepository.findOne(branchId);
		if(Objects.isNull(branch))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		if(branch.getManagerId().equals(managerId))
			return updateBranch(branchId);
		else
			throw new BusinessRuntimeException(ApplicationConstants.ERR_ACCESS_DENIED);
	}
	
	/**
	 * parent branch is fetched for a counter 
	 * branch's counter map is updated
	 * and entire branch object is updated in JVM cache
	 */
	@Override
	public Counter updateCounter(Counter counter) {
		Branch branch = getBranch(counter.getBranchId());
		if(Objects.isNull(branch))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_BRANCH_NOT_EXIST);
		branch.updateCounter(counter);
		return updateBranch(branch).getCounter(counter.getNumber());
	}
	
	/**
	 * All the counters within a branch serving passed customer type are short-listed. 
	 * Counters which are not serving passed service_step are removed from the list. 
	 * From the remaining list counter with least token queue size is returned
	 */
	@Override
	public Counter getBestCounter(Long branchId, CustomerType type, Long stepId) {
		List<Counter> counters;
		List<Counter> counters1 = new ArrayList<>();
		Branch branch = getBranch(branchId);
		if(type == CustomerType.REGULAR)
			counters = branch.getRegularCounters();
		else
			counters = branch.getPremiumCounters();
		
		for(Counter counter: counters) {
			for(ServiceStep step: counter.getSteps()) {
				if(step.getId() == stepId) {
					counters1.add(counter);
					break;
				}
			}
		}
		
		int leastSize = Integer.MAX_VALUE;
		Counter counter = null;
		
		for(Counter counter1: counters1) {
			if(leastSize > counter1.getTokens().size()) {
				leastSize = counter1.getTokens().size();
				counter = counter1;
			}
		}
		return counter;
	}
	
	/**
	 * cache is cleared and initial startup operation is called
	 */
	@Override
	public void reloadEntireCache() {
		branches.clear();
		getAllBranches();
	}
	
	/**
	 * updates a branch in the cache
	 * 
	 * @param branch
	 * @return updated Branch instance
	 */
	private Branch updateBranch(Branch branch) {
		branches.put(branch.getId(), branch);
		return branch;
	}
	
	/**
	 * updates a branch in the cache by fetching it from DB synchronously
	 * Branch is immediately removed from cache so that all operations fail till it is fetched from DB
	 * it's like making branch and its entire children unavailable till it is ready again.
	 * 
	 * @param branchId
	 * @return updated Branch instance
	 */
	private Branch updateBranch(Long branchId) {
		branches.remove(branchId);
		
		Branch branch = branchRepository.findOne(branchId);
		
		//putting back updated instance
		branches.put(branchId, getBranchFull(branch));
		
		return branches.get(branchId);
	}
	
	/**
	 * For full cache load
	 * 
	 * @return list of branches
	 */
	private List<Branch> getBranchesFromDB() {
		List<Branch> branchList = branchRepository.findAll();
		List<Branch> resultList = new ArrayList<>();
		
		branchList.forEach(branch -> resultList.add(getBranchFull(branch)));
		
		return resultList;
	}
	
	/**
	 * To get full fledged branch with all its containing objects
	 * Heavy operation but called sparingly
	 * 
	 * services are fetched
	 * counters are fetched
	 * branch token generator base value is fetched
	 * 
	 * @param branch
	 * @return full branch instance
	 */
	private Branch getBranchFull(Branch branch) {
		branch.setPremiumServices(getServicesFromDB(branch.getId(), CustomerType.PREMIUM));
		branch.setRegularServices(getServicesFromDB(branch.getId(), CustomerType.REGULAR));
		List<Counter> counters = counterService.getBranchCountersFromDB(branch.getId());
		branch.setTokenNumber(tokenRepository.getMaxCounterNumber(branch.getId()));
		
		counters.forEach(branch::updateCounter);
		
		return branch;
	}
	
	/**
	 * For service + service step load for a branch and customer type
	 * 
	 * @param branchId
	 * @param type
	 * @return
	 */
	private List<Service> getServicesFromDB(Long branchId, CustomerType type) {
		return serviceRepository.getServicesForBranch(branchId, type);
	}
}
