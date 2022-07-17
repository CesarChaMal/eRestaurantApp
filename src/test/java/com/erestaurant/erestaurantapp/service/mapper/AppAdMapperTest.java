package com.erestaurant.erestaurantapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AppAdMapperTest {

    private AppAdMapper appAdMapper;

    @BeforeEach
    public void setUp() {
        appAdMapper = new AppAdMapperImpl();
    }
}
