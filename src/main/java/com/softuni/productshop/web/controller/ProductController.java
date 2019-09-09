package com.softuni.productshop.web.controller;

import com.softuni.productshop.domain.model.binding.ProductAddBindingModel;
import com.softuni.productshop.domain.model.service.ProductServiceModel;
import com.softuni.productshop.domain.model.view.ProductAllViewModel;
import com.softuni.productshop.domain.model.view.ProductDetailsViewModel;
import com.softuni.productshop.error.ProductNameAlreadyExistsException;
import com.softuni.productshop.error.ProductNotFoundException;
import com.softuni.productshop.service.CategoryService;
import com.softuni.productshop.service.CloudinaryService;
import com.softuni.productshop.service.ProductService;
import com.softuni.productshop.web.annotations.PageTitle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
public class ProductController extends BaseController {

    private final ModelMapper modelMapper;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public ProductController(ModelMapper modelMapper,
                             ProductService productService,
                             CategoryService categoryService,
                             CloudinaryService cloudinaryService) {
        this.modelMapper = modelMapper;
        this.productService = productService;
        this.categoryService = categoryService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("Add Product")
    public ModelAndView addProduct() {
        return view("product/add-product");
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addProductConfirm(@ModelAttribute ProductAddBindingModel model) throws IOException {
        ProductServiceModel productServiceModel = modelMapper.map(model, ProductServiceModel.class);
        productServiceModel.setCategories(
                categoryService.findAllCategories()
                        .stream()
                        .filter(category -> model.getCategories().contains(category.getId()))
                        .collect(Collectors.toList())
        );
        productServiceModel.setImageUrl(
                cloudinaryService.uploadImage(model.getImage())
        );

        productService.createProduct(productServiceModel);

        return redirect("/products/all");
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("All Products")
    public ModelAndView allProducts(ModelAndView modelAndView) {
        modelAndView.addObject("products", productService.findAllProducts()
                .stream()
                .map(product -> modelMapper.map(product, ProductAllViewModel.class))
                .collect(Collectors.toList()));

        return view("product/all-products", modelAndView);
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("isAuthenticated()")
    @PageTitle("Product Details")
    public ModelAndView detailsProduct(@PathVariable String id, ModelAndView modelAndView) {
        ProductDetailsViewModel model = modelMapper.map(productService.findProductById(id), ProductDetailsViewModel.class);

        modelAndView.addObject("product", model);

        return view("product/details", modelAndView);
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("Edit Product")
    public ModelAndView editProduct(@PathVariable String id, ModelAndView modelAndView) {
        ProductServiceModel productServiceModel = productService.findProductById(id);
        ProductAddBindingModel model = modelMapper.map(productServiceModel, ProductAddBindingModel.class);
        model.setCategories(productServiceModel.getCategories().stream().map(category -> category.getName()).collect(Collectors.toList()));

        modelAndView.addObject("product", model);
        modelAndView.addObject("productId", id);

        return view("product/edit-product", modelAndView);
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView editProductConfirm(@PathVariable String id,
                                           @ModelAttribute ProductAddBindingModel model) {
        productService.editProduct(id, modelMapper.map(model, ProductServiceModel.class));

        return redirect("/products/details/" + id);
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @PageTitle("Delete Product")
    public ModelAndView deleteProduct(@PathVariable String id, ModelAndView modelAndView) {
        ProductServiceModel productServiceModel = productService.findProductById(id);
        ProductAddBindingModel model = modelMapper.map(productServiceModel, ProductAddBindingModel.class);
        model.setCategories(productServiceModel.getCategories().stream().map(c -> c.getName()).collect(Collectors.toList()));

        modelAndView.addObject("product", model);
        modelAndView.addObject("productId", id);

        return view("product/delete-product", modelAndView);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView deleteProductConfirm(@PathVariable String id) {
        productService.deleteProduct(id);

        return redirect("/products/all");
    }

    @GetMapping("/fetch/{category}")
    @ResponseBody
    public List<ProductAllViewModel> fetchByCategory(@PathVariable String category) {
        if(category.equals("all")) {
            return productService.findAllProducts()
                    .stream()
                    .map(product -> modelMapper.map(product, ProductAllViewModel.class))
                    .collect(Collectors.toList());
        }

        return productService.findAllByCategory(category)
                .stream()
                .map(product -> modelMapper.map(product, ProductAllViewModel.class))
                .collect(Collectors.toList());
    }

    @ExceptionHandler({ProductNotFoundException.class})
    public ModelAndView handleProductNotFound(ProductNotFoundException e) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", e.getMessage());
        modelAndView.addObject("statusCode", e.getStatusCode());

        return modelAndView;
    }

    @ExceptionHandler({ProductNameAlreadyExistsException.class})
    public ModelAndView handleProductNameAlreadyExist(ProductNameAlreadyExistsException e) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("message", e.getMessage());
        modelAndView.addObject("statusCode", e.getStatusCode());

        return modelAndView;
    }
}
