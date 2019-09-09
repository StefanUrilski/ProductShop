package com.softuni.productshop.service;

import com.softuni.productshop.common.Constants;
import org.modelmapper.ModelMapper;
import com.softuni.productshop.domain.entity.Category;
import com.softuni.productshop.domain.model.service.CategoryServiceModel;
import com.softuni.productshop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper, Validator validator) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.validator = validator;
    }

    @Override
    public CategoryServiceModel addCategory(CategoryServiceModel categoryServiceModel) {
        if (!validator.validate(categoryServiceModel).isEmpty()) {
            throw new IllegalArgumentException(Constants.INVALID_CATEGORY);
        }

        Category category = modelMapper.map(categoryServiceModel, Category.class);

        return modelMapper.map(categoryRepository.saveAndFlush(category), CategoryServiceModel.class);
    }

    @Override
    public List<CategoryServiceModel> findAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(c -> modelMapper.map(c, CategoryServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryServiceModel findCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(Constants.ID_NOT_FOUND));

        return modelMapper.map(category, CategoryServiceModel.class);
    }

    @Override
    public CategoryServiceModel editCategory(String id, CategoryServiceModel categoryServiceModel) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(Constants.ID_NOT_FOUND));

        category.setName(categoryServiceModel.getName());

        return modelMapper.map(categoryRepository.saveAndFlush(category), CategoryServiceModel.class);
    }

    @Override
    public CategoryServiceModel deleteCategory(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(Constants.ID_NOT_FOUND));

        categoryRepository.delete(category);

        return modelMapper.map(category, CategoryServiceModel.class);
    }
}
