package com.softuni.productshop.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    private String name;

    @Column(name = "category_name",nullable = false, unique = true, updatable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

