package com.erestaurant.erestaurantapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.erestaurantapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderType.class);
        OrderType orderType1 = new OrderType();
        orderType1.setId("id1");
        OrderType orderType2 = new OrderType();
        orderType2.setId(orderType1.getId());
        assertThat(orderType1).isEqualTo(orderType2);
        orderType2.setId("id2");
        assertThat(orderType1).isNotEqualTo(orderType2);
        orderType1.setId(null);
        assertThat(orderType1).isNotEqualTo(orderType2);
    }
}
