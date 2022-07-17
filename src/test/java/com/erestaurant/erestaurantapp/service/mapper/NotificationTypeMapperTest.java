package com.erestaurant.erestaurantapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NotificationTypeMapperTest {

    private NotificationTypeMapper notificationTypeMapper;

    @BeforeEach
    public void setUp() {
        notificationTypeMapper = new NotificationTypeMapperImpl();
    }
}
