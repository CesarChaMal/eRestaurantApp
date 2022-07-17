package com.erestaurant.erestaurantapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.erestaurantapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RestaurantAdDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestaurantAdDTO.class);
        RestaurantAdDTO restaurantAdDTO1 = new RestaurantAdDTO();
        restaurantAdDTO1.setId("id1");
        RestaurantAdDTO restaurantAdDTO2 = new RestaurantAdDTO();
        assertThat(restaurantAdDTO1).isNotEqualTo(restaurantAdDTO2);
        restaurantAdDTO2.setId(restaurantAdDTO1.getId());
        assertThat(restaurantAdDTO1).isEqualTo(restaurantAdDTO2);
        restaurantAdDTO2.setId("id2");
        assertThat(restaurantAdDTO1).isNotEqualTo(restaurantAdDTO2);
        restaurantAdDTO1.setId(null);
        assertThat(restaurantAdDTO1).isNotEqualTo(restaurantAdDTO2);
    }
}
