package com.erestaurant.erestaurantapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestaurantMapperTest {

    private RestaurantMapper restaurantMapper;

    @BeforeEach
    public void setUp() {
        restaurantMapper = new RestaurantMapperImpl();
    }
}
