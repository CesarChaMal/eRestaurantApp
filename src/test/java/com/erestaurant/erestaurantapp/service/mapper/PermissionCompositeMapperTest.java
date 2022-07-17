package com.erestaurant.erestaurantapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PermissionCompositeMapperTest {

    private PermissionCompositeMapper permissionCompositeMapper;

    @BeforeEach
    public void setUp() {
        permissionCompositeMapper = new PermissionCompositeMapperImpl();
    }
}
