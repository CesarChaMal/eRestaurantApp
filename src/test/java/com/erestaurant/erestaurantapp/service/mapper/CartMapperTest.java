package com.erestaurant.erestaurantapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartMapperTest {

    private CartMapper cartMapper;

    @BeforeEach
    public void setUp() {
        cartMapper = new CartMapperImpl();
    }
}
