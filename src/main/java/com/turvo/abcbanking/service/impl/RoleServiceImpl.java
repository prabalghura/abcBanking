package com.turvo.abcbanking.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.model.Role;
import com.turvo.abcbanking.model.UserXRole;
import com.turvo.abcbanking.repository.RoleRepository;
import com.turvo.abcbanking.repository.UserRepository;
import com.turvo.abcbanking.repository.UserXRoleRepository;
import com.turvo.abcbanking.service.RoleService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Service implementation for Role operations
 * 
 * @author Prabal Ghura
 *
 */
@Service("roleService")
public class RoleServiceImpl extends BaseServiceImpl implements RoleService{
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	UserXRoleRepository userXroleRepository;

	@Override
	public Boolean checkAccessForUser(String userId, String role) {
		return roleRepository.checkAccess(userId, role) != 0;
	}
	
	/**
	 * userId is validated against DB, exception is thrown if found none
	 * roles are fetched from DB and returned.
	 */
	@Override
	public List<Role> getUserRoles(String userId) {
		if(!userRepository.exists(userId))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_USER_NOT_EXIST);
		return roleRepository.getRolesForUser(userId);
	}

	/**
	 * Access is checked 
	 * userId is validated against DB, exception is thrown if found none
	 * 
	 * only roles which exist in DB and currently not possessed by user are updated in DB.
	 */
	@Override
	@Transactional(readOnly = false)
	public List<Role> assignRolesToUser(String assignerId, String userId, List<Role> roles) {
		checkAccess(assignerId, ApplicationConstants.ROLE_ASSIGN_ROLES);
		if(!userRepository.exists(userId))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_USER_NOT_EXIST);
		List<String> names = roles.stream().map(Role::getName).collect(Collectors.toList());
		List<Role> assignableRoles = roleRepository.findByNameIn(names);
		List<UserXRole> assignedRoles = userXroleRepository.findByUserId(userId);
		
		Set<Integer> assignedIds = assignedRoles.stream()
		        .map(UserXRole::getRoleId)
		        .collect(Collectors.toSet());
		
		List<Integer> rolesToBeAssigned = assignableRoles.stream().filter(role -> !assignedIds.contains(role.getId())).map(Role::getId).collect(Collectors.toList());
		
		List<UserXRole> toBeAssignedRoles = new ArrayList<>();
		
		rolesToBeAssigned.forEach(b -> toBeAssignedRoles.add(new UserXRole(userId, b, assignerId)));
		
		userXroleRepository.save(toBeAssignedRoles);
		userXroleRepository.flush();
		
		return getUserRoles(userId);
	}
	
	/**
	 * All roles are fetched from DB and returned
	 */
	@Override
	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}
}
