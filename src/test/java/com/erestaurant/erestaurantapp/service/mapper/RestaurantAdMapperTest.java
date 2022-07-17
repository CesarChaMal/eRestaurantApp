package com.erestaurant.erestaurantapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestaurantAdMapperTest {

    private RestaurantAdMapper restaurantAdMapper;

    @BeforeEach
    public void setUp() {
        restaurantAdMapper = new RestaurantAdMapperImpl();
    }
}
