package com.erestaurant.erestaurantapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.erestaurantapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RestaurantAdTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestaurantAd.class);
        RestaurantAd restaurantAd1 = new RestaurantAd();
        restaurantAd1.setId("id1");
        RestaurantAd restaurantAd2 = new RestaurantAd();
        restaurantAd2.setId(restaurantAd1.getId());
        assertThat(restaurantAd1).isEqualTo(restaurantAd2);
        restaurantAd2.setId("id2");
        assertThat(restaurantAd1).isNotEqualTo(restaurantAd2);
        restaurantAd1.setId(null);
        assertThat(restaurantAd1).isNotEqualTo(restaurantAd2);
    }
}
