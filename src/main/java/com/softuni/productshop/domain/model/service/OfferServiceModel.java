package com.softuni.productshop.domain.model.service;

import com.softuni.productshop.domain.entity.Product;

import java.math.BigDecimal;

public class OfferServiceModel extends BaseServiceModel {

    private ProductServiceModel product;
    private BigDecimal price;

    public OfferServiceModel() {
    }

    public ProductServiceModel getProduct() {
        return product;
    }

    public void setProduct(ProductServiceModel product) {
        this.product = product;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
