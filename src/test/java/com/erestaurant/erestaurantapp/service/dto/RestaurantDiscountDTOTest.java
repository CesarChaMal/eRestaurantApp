package com.erestaurant.erestaurantapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.erestaurantapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RestaurantDiscountDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestaurantDiscountDTO.class);
        RestaurantDiscountDTO restaurantDiscountDTO1 = new RestaurantDiscountDTO();
        restaurantDiscountDTO1.setId("id1");
        RestaurantDiscountDTO restaurantDiscountDTO2 = new RestaurantDiscountDTO();
        assertThat(restaurantDiscountDTO1).isNotEqualTo(restaurantDiscountDTO2);
        restaurantDiscountDTO2.setId(restaurantDiscountDTO1.getId());
        assertThat(restaurantDiscountDTO1).isEqualTo(restaurantDiscountDTO2);
        restaurantDiscountDTO2.setId("id2");
        assertThat(restaurantDiscountDTO1).isNotEqualTo(restaurantDiscountDTO2);
        restaurantDiscountDTO1.setId(null);
        assertThat(restaurantDiscountDTO1).isNotEqualTo(restaurantDiscountDTO2);
    }
}
