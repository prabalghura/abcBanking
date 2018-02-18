package com.turvo.abcbanking.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

/**
 * Service implementation for Role operations
 * 
 * @author Prabal Ghura
 *
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService{
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	UserXRoleRepository userXroleRepository;

	@Override
	public void checkAccess(String userId, String role) {
		if(Objects.isNull(userId) || userId.equals(""))
			throw new BusinessRuntimeException("Operation needs authorization");
		if(roleRepository.checkAccess(userId, role) == 0)
			throw new BusinessRuntimeException("Access Denied");
	}
	
	@Override
	public List<Role> getUserRoles(String userId) {
		if(!userRepository.exists(userId))
			throw new BusinessRuntimeException("User doesn't exist.");
		return roleRepository.getRolesForUser(userId);
	}

	@Override
	@Transactional(readOnly = false)
	public List<Role> assignRolesToUser(String assignerId, String userId, List<Role> roles) {
		checkAccess(assignerId, "ASSIGN_ROLES");
		if(!userRepository.exists(userId))
			throw new BusinessRuntimeException("User doesn't exist.");
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
	
	@Override
	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}
}
