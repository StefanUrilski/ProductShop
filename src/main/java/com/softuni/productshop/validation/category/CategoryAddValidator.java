package com.softuni.productshop.validation.category;

import com.softuni.productshop.domain.model.binding.CategoryAddBindingModel;
import com.softuni.productshop.repository.CategoryRepository;
import com.softuni.productshop.validation.ValidationConstants;
import com.softuni.productshop.validation.annotation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

@Validator
public class CategoryAddValidator implements org.springframework.validation.Validator {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryAddValidator(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return CategoryAddBindingModel.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        CategoryAddBindingModel categoryAddBindingModel = (CategoryAddBindingModel) o;

        if (categoryAddBindingModel.getName().length() < 3) {
            errors.rejectValue(
                    "name",
                    ValidationConstants.NAME_LENGTH,
                    ValidationConstants.NAME_LENGTH
            );
        }

        if (categoryRepository.findByName(categoryAddBindingModel.getName()).isPresent()) {
            errors.rejectValue(
                    "name",
                    String.format(ValidationConstants.NAME_ALREADY_EXISTS, "Category", categoryAddBindingModel.getName()),
                    String.format(ValidationConstants.NAME_ALREADY_EXISTS, "Category", categoryAddBindingModel.getName())
            );
        }
    }
}
