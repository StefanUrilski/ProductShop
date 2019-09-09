package com.softuni.productshop.web.controller;

import com.softuni.productshop.domain.model.view.CategoryViewModel;
import com.softuni.productshop.service.CategoryService;
import com.softuni.productshop.web.annotations.PageTitle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController extends BaseController {

    private final ModelMapper modelMapper;
    private final CategoryService categoryService;

    @Autowired
    public HomeController(ModelMapper modelMapper,
                          CategoryService categoryService) {
        this.modelMapper = modelMapper;
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    @PreAuthorize("isAnonymous()")
    @PageTitle("Index")
    public ModelAndView index() {
        return view("index");
    }

    @GetMapping("/home")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Home")
    public ModelAndView home(ModelAndView modelAndView) {
        List<CategoryViewModel> categories = categoryService.findAllCategories()
                .stream()
                .map(category -> modelMapper.map(category, CategoryViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject("categories", categories);

        return view("home", modelAndView);
    }
}
