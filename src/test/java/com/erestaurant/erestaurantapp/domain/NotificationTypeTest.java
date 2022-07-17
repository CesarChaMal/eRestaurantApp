package com.erestaurant.erestaurantapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.erestaurantapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificationTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationType.class);
        NotificationType notificationType1 = new NotificationType();
        notificationType1.setId("id1");
        NotificationType notificationType2 = new NotificationType();
        notificationType2.setId(notificationType1.getId());
        assertThat(notificationType1).isEqualTo(notificationType2);
        notificationType2.setId("id2");
        assertThat(notificationType1).isNotEqualTo(notificationType2);
        notificationType1.setId(null);
        assertThat(notificationType1).isNotEqualTo(notificationType2);
    }
}
