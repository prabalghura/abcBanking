package com.turvo.abcbanking.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turvo.abcbanking.exception.BusinessRuntimeException;
import com.turvo.abcbanking.model.User;
import com.turvo.abcbanking.repository.UserRepository;
import com.turvo.abcbanking.service.UserService;
import com.turvo.abcbanking.utils.ApplicationConstants;

/**
 * Service implementation for User operations
 * 
 * @author Prabal Ghura
 *
 */
@Service("userService")
public class UserServiceImpl extends BaseServiceImpl implements UserService{

	@Autowired
	UserRepository userRepository;
	
	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User getUser(String id) {
		return userRepository.findOne(id);
	}

	@Override
	@Transactional(readOnly = false)
	public User createNewUser(String creatorId, User user) {
		checkAccess(creatorId, ApplicationConstants.ROLE_ADD_NEW_USER);
		if(userRepository.exists(user.getUserId()))
			throw new BusinessRuntimeException(ApplicationConstants.ERR_USER_ID_EXIST);
		user.setCreatedBy(creatorId);
		return userRepository.saveAndFlush(user);
	}
}
