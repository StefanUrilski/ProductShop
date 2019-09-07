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
import java.util.Set;
import java.util.stream.Collectors;

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
    public boolean registerUser(UserServiceModel userServiceModel) {
        roleService.seedRolesInDb();

        Set<Role> roles = new HashSet<>();
        if (userRepository.count() == 0) {
            roles = roleService.findAllRoles().stream()
                    .map(role -> modelMapper.map(role, Role.class))
                    .collect(Collectors.toSet());
        } else {
            roles.add(modelMapper.map(
                    roleService.findByRole(Constants.ROLE_USER),
                    Role.class
            ));
        }

        User user = modelMapper.map(userServiceModel, User.class);

        user.setAuthorities(roles);
        user.setPassword(encoder.encode(user.getPassword()));

        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(Constants.USERNAME_NOT_FOUND));
    }

    @Override
    public UserServiceModel findByUsername(String username) {
        return modelMapper.map(userRepository.findByUsername(username), UserServiceModel.class);
    }
}
