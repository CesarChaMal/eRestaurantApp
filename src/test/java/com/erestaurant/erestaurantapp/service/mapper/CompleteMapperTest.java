package com.erestaurant.erestaurantapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CompleteMapperTest {

    private CompleteMapper completeMapper;

    @BeforeEach
    public void setUp() {
        completeMapper = new CompleteMapperImpl();
    }
}
