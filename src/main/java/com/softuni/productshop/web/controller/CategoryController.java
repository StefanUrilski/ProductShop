package com.softuni.productshop.web.controller;

import org.modelmapper.ModelMapper;
import com.softuni.productshop.domain.model.binding.CategoryAddBindingModel;
import com.softuni.productshop.domain.model.binding.CategoryEditBindingModel;
import com.softuni.productshop.domain.model.service.CategoryServiceModel;
import com.softuni.productshop.domain.model.view.CategoryViewModel;
import com.softuni.productshop.service.CategoryService;
import com.softuni.productshop.validation.category.CategoryAddValidator;
import com.softuni.productshop.validation.category.CategoryEditValidator;
import com.softuni.productshop.web.annotations.PageTitle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/categories")
public class CategoryController extends BaseController {

    private final ModelMapper modelMapper;
    private final CategoryService categoryService;
    private final CategoryAddValidator addValidator;
    private final CategoryEditValidator editValidator;

    @Autowired
    public CategoryController(ModelMapper modelMapper,
                              CategoryService categoryService,
                              CategoryAddValidator addValidator,
                              CategoryEditValidator editValidator) {
        this.modelMapper = modelMapper;
        this.categoryService = categoryService;
        this.addValidator = addValidator;
        this.editValidator = editValidator;
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("Add Category")
    public ModelAndView addCategory(ModelAndView modelAndView,
                                    @ModelAttribute(name = "model") CategoryAddBindingModel model) {
        modelAndView.addObject("model", model);

        return view("category/add-category", modelAndView);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addCategoryConfirm(ModelAndView modelAndView,
                                           @ModelAttribute(name = "model") CategoryAddBindingModel model,
                                           BindingResult bindingResult) {
        addValidator.validate(model, bindingResult);

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("model", model);

            return view("category/add-category", modelAndView);
        }

        CategoryServiceModel categoryServiceModel = modelMapper.map(model, CategoryServiceModel.class);
        categoryService.addCategory(categoryServiceModel);

        return redirect("/categories/all");
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("All Categories")
    public ModelAndView allCategories(ModelAndView modelAndView) {
        List<CategoryViewModel> categories = categoryService.findAllCategories()
                .stream()
                .map(category -> modelMapper.map(category, CategoryViewModel.class))
                .collect(Collectors.toList());

        modelAndView.addObject("categories", categories);

        return view("category/all-categories", modelAndView);
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("Edit Category")
    public ModelAndView editCategory(@PathVariable String id,
                                     ModelAndView modelAndView,
                                     @ModelAttribute(name = "model") CategoryEditBindingModel model) {
        model = modelMapper.map(categoryService.findCategoryById(id), CategoryEditBindingModel.class);

        modelAndView.addObject("categoryId", id);
        modelAndView.addObject("model", model);

        return view("category/edit-category", modelAndView);
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView editCategoryConfirm(@PathVariable String id,
                                            ModelAndView modelAndView,
                                            @ModelAttribute(name = "model") CategoryEditBindingModel model,
                                            BindingResult bindingResult) {
        editValidator.validate(model, bindingResult);

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("categoryId", id);
            modelAndView.addObject("model", model);

            return view("category/edit-category", modelAndView);
        }

        categoryService.editCategory(id, modelMapper.map(model, CategoryServiceModel.class));

        return redirect("/categories/all");
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("Delete Category")
    public ModelAndView deleteCategory(@PathVariable String id, ModelAndView modelAndView) {
        CategoryViewModel category = modelMapper.map(categoryService.findCategoryById(id), CategoryViewModel.class);
        modelAndView.addObject("model", category);

        return view("category/delete-category", modelAndView);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView deleteCategoryConfirm(@PathVariable String id) {
        categoryService.deleteCategory(id);

        return redirect("/categories/all");
    }

    @GetMapping("/fetch")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @ResponseBody
    public List<CategoryViewModel> fetchCategories() {
        return categoryService.findAllCategories()
                .stream()
                .map(category -> modelMapper.map(category, CategoryViewModel.class))
                .collect(Collectors.toList());
    }
}
