package com.softuni.productshop.service;

import com.softuni.productshop.domain.model.service.UserServiceModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserServiceModel registerUser(UserServiceModel userServiceModel);

    UserServiceModel findByUsername(String username);

    void firstLogin(String id);

    UserServiceModel editUser(UserServiceModel userServiceModel, String oldPassword);

    List<UserServiceModel> findAllUsers();

    void setUserRole(String id, String role);
}
