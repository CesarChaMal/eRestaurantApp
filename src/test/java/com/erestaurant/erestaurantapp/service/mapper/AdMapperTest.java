package com.erestaurant.erestaurantapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdMapperTest {

    private AdMapper adMapper;

    @BeforeEach
    public void setUp() {
        adMapper = new AdMapperImpl();
    }
}
