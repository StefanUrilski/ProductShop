package com.softuni.productshop.service;

import org.modelmapper.ModelMapper;
import com.softuni.productshop.domain.entity.Category;
import com.softuni.productshop.domain.entity.Product;
import com.softuni.productshop.domain.model.service.ProductServiceModel;
import com.softuni.productshop.error.ProductNameAlreadyExistsException;
import com.softuni.productshop.error.ProductNotFoundException;
import com.softuni.productshop.repository.OfferRepository;
import com.softuni.productshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.softuni.productshop.common.Constants.*;

@Service
public class ProductServiceImpl implements ProductService {

    private final ModelMapper modelMapper;
    private final OfferRepository offerRepository;
    private final CategoryService categoryService;
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(
            ModelMapper modelMapper,
            OfferRepository offerRepository,
            CategoryService categoryService,
            ProductRepository productRepository) {
        this.modelMapper = modelMapper;
        this.offerRepository = offerRepository;
        this.categoryService = categoryService;
        this.productRepository = productRepository;
    }

    @Override
    public ProductServiceModel createProduct(ProductServiceModel productServiceModel) {
        Product product = productRepository
                .findByName(productServiceModel.getName())
                .orElse(null);

        if (product != null) {
            throw new ProductNameAlreadyExistsException(PRODUCT_EXISTS);
        }

        product = modelMapper.map(productServiceModel, Product.class);
        product = productRepository.save(product);

        return modelMapper.map(product, ProductServiceModel.class);
    }

    @Override
    public List<ProductServiceModel> findAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(p -> modelMapper.map(p, ProductServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public ProductServiceModel findProductById(String id) {
        return productRepository.findById(id)
                .map(p -> {
                    ProductServiceModel productServiceModel = modelMapper.map(p, ProductServiceModel.class);
                    offerRepository.findByProduct_Id(productServiceModel.getId())
                            .ifPresent(o -> productServiceModel.setDiscountedPrice(o.getPrice()));

                    return productServiceModel;
                })
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_ID_NOT_FOUND));
    }

    @Override
    public ProductServiceModel editProduct(String id, ProductServiceModel productServiceModel) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_ID_NOT_FOUND));

        productServiceModel.setCategories(
                categoryService.findAllCategories()
                        .stream()
                        .filter(c -> productServiceModel.getCategories().contains(c.getId()))
                        .collect(Collectors.toList())
        );

        product.setName(productServiceModel.getName());
        product.setDescription(productServiceModel.getDescription());
        product.setPrice(productServiceModel.getPrice());
        product.setCategories(
                productServiceModel.getCategories()
                        .stream()
                        .map(c -> modelMapper.map(c, Category.class))
                        .collect(Collectors.toList())
        );

        offerRepository.findByProduct_Id(product.getId())
                .ifPresent((o) -> {
                    o.setPrice(product.getPrice().multiply(new BigDecimal(0.8)));

                    offerRepository.save(o);
                });

        return modelMapper.map(productRepository.saveAndFlush(product), ProductServiceModel.class);
    }

    @Override
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id).orElseThrow(()
                -> new ProductNotFoundException(PRODUCT_ID_NOT_FOUND));

        productRepository.delete(product);
    }

    @Override
    public List<ProductServiceModel> findAllByCategory(String category) {
        return productRepository.findAll()
                .stream()
                .filter(product -> product.getCategories().stream().anyMatch(categoryStream -> categoryStream.getName().equals(category)))
                .map(product -> modelMapper.map(product, ProductServiceModel.class))
                .collect(Collectors.toList());
    }
}
