package com.erestaurant.erestaurantapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.erestaurantapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RestaurantDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestaurantDTO.class);
        RestaurantDTO restaurantDTO1 = new RestaurantDTO();
        restaurantDTO1.setId("id1");
        RestaurantDTO restaurantDTO2 = new RestaurantDTO();
        assertThat(restaurantDTO1).isNotEqualTo(restaurantDTO2);
        restaurantDTO2.setId(restaurantDTO1.getId());
        assertThat(restaurantDTO1).isEqualTo(restaurantDTO2);
        restaurantDTO2.setId("id2");
        assertThat(restaurantDTO1).isNotEqualTo(restaurantDTO2);
        restaurantDTO1.setId(null);
        assertThat(restaurantDTO1).isNotEqualTo(restaurantDTO2);
    }
}
