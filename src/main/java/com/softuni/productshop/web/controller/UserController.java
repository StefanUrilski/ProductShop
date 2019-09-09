package com.softuni.productshop.web.controller;

import com.softuni.productshop.domain.model.binding.UserEditBindingModel;
import com.softuni.productshop.domain.model.binding.UserRegisterBindingModel;
import com.softuni.productshop.domain.model.service.RoleServiceModel;
import com.softuni.productshop.domain.model.service.UserServiceModel;
import com.softuni.productshop.domain.model.view.UserAllViewModel;
import com.softuni.productshop.domain.model.view.UserProfileViewModel;
import com.softuni.productshop.service.UserService;
import com.softuni.productshop.validation.user.UserEditValidator;
import com.softuni.productshop.validation.user.UserRegisterValidator;
import com.softuni.productshop.web.annotations.PageTitle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UserController extends BaseController {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final UserEditValidator userEditValidator;
    private final UserRegisterValidator userRegisterValidator;

    @Autowired
    public UserController(ModelMapper modelMapper,
                          UserService userService,
                          UserEditValidator userEditValidator,
                          UserRegisterValidator userRegisterValidator) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.userEditValidator = userEditValidator;
        this.userRegisterValidator = userRegisterValidator;
    }

    @GetMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView register() {
        return view("user/register");
    }

    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView registerConfirm(ModelAndView modelAndView,
                                        @ModelAttribute(name = "model") UserRegisterBindingModel model,
                                        BindingResult bindingResult) {
        userRegisterValidator.validate(model, bindingResult);

        if (bindingResult.hasErrors()) {
            model.setPassword(null);
            model.setConfirmPassword(null);
            modelAndView.addObject("model", model);
            return view("user/register", modelAndView);
        }

        UserServiceModel userServiceModel = modelMapper.map(model, UserServiceModel.class);
        this.userService.registerUser(userServiceModel);

        return redirect("/login");
    }

    @GetMapping("/login")
    @PreAuthorize("isAnonymous()")
    public ModelAndView login() {
        return view("user/login");
    }

    @GetMapping("/login/profile")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView firstLogin(Principal principal, ModelAndView modelAndView) {
        UserServiceModel loggedUser = userService.findByUsername(principal.getName());

        if (loggedUser.isFirstTimeLogin()) {
            return redirect("/home");
        }

        modelAndView.addObject("user", modelMapper.map(loggedUser, UserProfileViewModel.class));
        userService.firstLogin(loggedUser.getId());

        return view("/user/profile", modelAndView);
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView profile(Principal principal, ModelAndView modelAndView) {
        UserServiceModel loggedUser = userService.findByUsername(principal.getName());

        modelAndView.addObject("user", modelMapper.map(loggedUser, UserProfileViewModel.class));

        return view("/user/profile", modelAndView);
    }

    @GetMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView editProfile(Principal principal,
                                    ModelAndView modelAndView,
                                    @ModelAttribute(name = "model") UserEditBindingModel model) {

        UserServiceModel userServiceModel = userService.findByUsername(principal.getName());
        model = modelMapper.map(userServiceModel, UserEditBindingModel.class);
        model.setPassword(null);
        modelAndView.addObject("model", model);

        return view("user/edit-profile", modelAndView);
    }

    @PatchMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView editProfileConfirm(ModelAndView modelAndView,
                                           @ModelAttribute(name = "model") UserEditBindingModel model,
                                           BindingResult bindingResult) {

        userEditValidator.validate(model, bindingResult);

        if (bindingResult.hasErrors()) {
            model.setOldPassword(null);
            model.setPassword(null);
            model.setConfirmPassword(null);
            modelAndView.addObject("model", model);

            return view("user/edit-profile", modelAndView);
        }

        UserServiceModel userServiceModel = modelMapper.map(model, UserServiceModel.class);
        userService.editUser(userServiceModel, model.getOldPassword());

        return redirect("/user/profile");
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PageTitle("All Users")
    public ModelAndView allUsers(ModelAndView modelAndView) {
        List<UserAllViewModel> users = userService.findAllUsers()
                .stream()
                .map(user -> {
                    UserAllViewModel userModel = modelMapper.map(user, UserAllViewModel.class);
                    Set<String> authorities = user.getAuthorities().stream().map(RoleServiceModel::getAuthority).collect(Collectors.toSet());
                    userModel.setAuthorities(authorities);

                    return userModel;
                })
                .collect(Collectors.toList());

        modelAndView.addObject("users", users);

        return view("user/all-users", modelAndView);
    }

    @PostMapping("/set-user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setUser(@PathVariable String id) {
        userService.setUserRole(id, "user");

        return redirect("/users/all");
    }

    @PostMapping("/set-moderator/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setModerator(@PathVariable String id) {
        userService.setUserRole(id, "moderator");

        return redirect("/users/all");
    }

    @PostMapping("/set-admin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setAdmin(@PathVariable String id) {
        userService.setUserRole(id, "admin");

        return redirect("/users/all");
    }

    @InitBinder
    private void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
