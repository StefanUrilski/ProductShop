package com.softuni.productshop.service;

import com.softuni.productshop.domain.model.service.RoleServiceModel;

import java.util.Set;

public interface RoleService {

    void seedRolesInDb();

    RoleServiceModel findByRole(String authority);

    Set<RoleServiceModel> findAllRoles();
}
