package com.erestaurant.erestaurantapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderTypeMapperTest {

    private OrderTypeMapper orderTypeMapper;

    @BeforeEach
    public void setUp() {
        orderTypeMapper = new OrderTypeMapperImpl();
    }
}
