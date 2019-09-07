package com.softuni.productshop.service;

import com.softuni.productshop.domain.model.service.UserServiceModel;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    boolean registerUser(UserServiceModel userServiceModel);

    UserServiceModel findByUsername(String username);
}
