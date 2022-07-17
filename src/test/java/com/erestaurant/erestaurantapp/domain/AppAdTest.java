package com.erestaurant.erestaurantapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.erestaurantapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AppAdTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AppAd.class);
        AppAd appAd1 = new AppAd();
        appAd1.setId("id1");
        AppAd appAd2 = new AppAd();
        appAd2.setId(appAd1.getId());
        assertThat(appAd1).isEqualTo(appAd2);
        appAd2.setId("id2");
        assertThat(appAd1).isNotEqualTo(appAd2);
        appAd1.setId(null);
        assertThat(appAd1).isNotEqualTo(appAd2);
    }
}
