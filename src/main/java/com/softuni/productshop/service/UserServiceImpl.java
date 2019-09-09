package com.softuni.productshop.service;

import com.softuni.productshop.common.Constants;
import com.softuni.productshop.domain.entity.Role;
import com.softuni.productshop.domain.entity.User;
import com.softuni.productshop.domain.model.service.UserServiceModel;
import com.softuni.productshop.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.softuni.productshop.common.Constants.*;

@Service
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final RoleService roleService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(ModelMapper modelMapper,
                           RoleService roleService,
                           UserRepository userRepository,
                           BCryptPasswordEncoder encoder) {
        this.modelMapper = modelMapper;
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public UserServiceModel registerUser(UserServiceModel userServiceModel) {
        roleService.seedRolesInDb();

        Set<Role> roles = new HashSet<>();
        if (userRepository.count() == 0) {
            roles = roleService.findAllRoles().stream()
                    .map(role -> modelMapper.map(role, Role.class))
                    .collect(Collectors.toSet());
        } else {
            roles.add(modelMapper.map(roleService.findByRole(ROLE_USER), Role.class));
        }

        User user = modelMapper.map(userServiceModel, User.class);

        user.setAuthorities(roles);
        user.setPassword(encoder.encode(user.getPassword()));

        return modelMapper.map(userRepository.saveAndFlush(user), UserServiceModel.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(Constants.USERNAME_NOT_FOUND));
    }

    @Override
    public UserServiceModel findByUsername(String username) {
        return modelMapper.map(
                userRepository.findByUsername(username).orElseThrow(()
                        -> new UsernameNotFoundException(Constants.USERNAME_NOT_FOUND)), UserServiceModel.class);
    }

    @Override
    public void firstLogin(String id) {
        User user = userRepository.findById(id).orElseThrow(()
                -> new IllegalArgumentException(Constants.ID_NOT_FOUND));

        user.setFirstTimeLogin(true);

        userRepository.saveAndFlush(user);
    }

    @Override
    public UserServiceModel editUser(UserServiceModel userServiceModel, String oldPassword) {
        User user = userRepository.findByUsername(userServiceModel.getUsername())
                .orElseThrow(()-> new UsernameNotFoundException(USERNAME_NOT_FOUND));

        if (!encoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException(INCORRECT_PASSWORD);
        }

        user.setEmail(userServiceModel.getEmail());
        user.setPassword(userServiceModel.getPassword() != null ?
                encoder.encode(userServiceModel.getPassword()) :
                user.getPassword());

        return modelMapper.map(userRepository.saveAndFlush(user), UserServiceModel.class);
    }

    @Override
    public List<UserServiceModel> findAllUsers() {
        return userRepository.findAll().stream().map(user -> modelMapper.map(user, UserServiceModel.class)).collect(Collectors.toList());
    }

    @Override
    public void setUserRole(String id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(ID_NOT_FOUND));

        UserServiceModel userServiceModel = modelMapper.map(user, UserServiceModel.class);
        userServiceModel.getAuthorities().clear();

        switch (role) {
            case "user":
                userServiceModel.getAuthorities().add(roleService.findByRole(ROLE_USER));
                break;
            case "moderator":
                userServiceModel.getAuthorities().add(roleService.findByRole(ROLE_USER));
                userServiceModel.getAuthorities().add(roleService.findByRole(ROLE_MODERATOR));
                break;
            case "admin":
                userServiceModel.getAuthorities().add(roleService.findByRole(ROLE_USER));
                userServiceModel.getAuthorities().add(roleService.findByRole(ROLE_ADMIN));
                userServiceModel.getAuthorities().add(roleService.findByRole(ROLE_MODERATOR));
                break;
        }

        userRepository.saveAndFlush(modelMapper.map(userServiceModel, User.class));
    }
}
