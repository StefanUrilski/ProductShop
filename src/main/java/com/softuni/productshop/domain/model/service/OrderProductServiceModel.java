package com.softuni.productshop.domain.model.service;

import java.math.BigDecimal;

public class OrderProductServiceModel extends BaseServiceModel {

    private ProductServiceModel product;
    private BigDecimal price;

    public OrderProductServiceModel() {
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
