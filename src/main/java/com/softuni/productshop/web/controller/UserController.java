package com.softuni.productshop.web.controller;

import com.softuni.productshop.domain.model.binding.UserRegisterBindingModel;
import com.softuni.productshop.domain.model.service.UserServiceModel;
import com.softuni.productshop.domain.model.view.UserProfileViewModel;
import com.softuni.productshop.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
@RequestMapping("/users")
public class UserController extends BaseController {

    private final ModelMapper modelMapper;
    private final UserService userService;

    @Autowired
    public UserController(ModelMapper modelMapper,
                          UserService userService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @GetMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView register() {
        return view("register");
    }

    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView registerConfirm(@ModelAttribute UserRegisterBindingModel model) {

        if (!model.getPassword().equals(model.getConfirmPassword())) {
            return view("register");
        }

        if (!userService.registerUser(modelMapper.map(model, UserServiceModel.class))) {
            return view("register");
        }
        return redirect("/users/login");
    }

    @GetMapping("/login")
    @PreAuthorize("isAnonymous()")
    public ModelAndView login() {
        return view("login");
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView profile(Principal principal, ModelAndView modelAndView) {
        UserServiceModel loggedUser = userService.findByUsername(principal.getName());

        if (loggedUser.isFirstTimeLogin()) {
            return redirect("/home");
        }

        modelAndView.addObject("user", modelMapper.map(loggedUser, UserProfileViewModel.class));

        return view("/profile", modelAndView);
    }
}
