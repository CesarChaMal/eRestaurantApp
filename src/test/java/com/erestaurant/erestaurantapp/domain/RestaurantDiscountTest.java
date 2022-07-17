package com.erestaurant.erestaurantapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.erestaurantapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RestaurantDiscountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestaurantDiscount.class);
        RestaurantDiscount restaurantDiscount1 = new RestaurantDiscount();
        restaurantDiscount1.setId("id1");
        RestaurantDiscount restaurantDiscount2 = new RestaurantDiscount();
        restaurantDiscount2.setId(restaurantDiscount1.getId());
        assertThat(restaurantDiscount1).isEqualTo(restaurantDiscount2);
        restaurantDiscount2.setId("id2");
        assertThat(restaurantDiscount1).isNotEqualTo(restaurantDiscount2);
        restaurantDiscount1.setId(null);
        assertThat(restaurantDiscount1).isNotEqualTo(restaurantDiscount2);
    }
}
