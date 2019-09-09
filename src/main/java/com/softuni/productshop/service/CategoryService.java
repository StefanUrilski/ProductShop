package com.softuni.productshop.service;

import com.softuni.productshop.domain.model.service.CategoryServiceModel;

import java.util.List;

public interface CategoryService {

    CategoryServiceModel addCategory(CategoryServiceModel categoryServiceModel);

    List<CategoryServiceModel> findAllCategories();

    CategoryServiceModel findCategoryById(String id);

    CategoryServiceModel editCategory(String id, CategoryServiceModel categoryServiceModel);

    CategoryServiceModel deleteCategory(String id);
}
