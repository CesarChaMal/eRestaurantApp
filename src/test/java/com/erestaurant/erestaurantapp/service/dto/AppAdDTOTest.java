package com.erestaurant.erestaurantapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.erestaurantapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AppAdDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppAdDTO.class);
        AppAdDTO appAdDTO1 = new AppAdDTO();
        appAdDTO1.setId("id1");
        AppAdDTO appAdDTO2 = new AppAdDTO();
        assertThat(appAdDTO1).isNotEqualTo(appAdDTO2);
        appAdDTO2.setId(appAdDTO1.getId());
        assertThat(appAdDTO1).isEqualTo(appAdDTO2);
        appAdDTO2.setId("id2");
        assertThat(appAdDTO1).isNotEqualTo(appAdDTO2);
        appAdDTO1.setId(null);
        assertThat(appAdDTO1).isNotEqualTo(appAdDTO2);
    }
}
