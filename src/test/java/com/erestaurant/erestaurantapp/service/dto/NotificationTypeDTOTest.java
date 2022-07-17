package com.erestaurant.erestaurantapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.erestaurantapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificationTypeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NotificationTypeDTO.class);
        NotificationTypeDTO notificationTypeDTO1 = new NotificationTypeDTO();
        notificationTypeDTO1.setId("id1");
        NotificationTypeDTO notificationTypeDTO2 = new NotificationTypeDTO();
        assertThat(notificationTypeDTO1).isNotEqualTo(notificationTypeDTO2);
        notificationTypeDTO2.setId(notificationTypeDTO1.getId());
        assertThat(notificationTypeDTO1).isEqualTo(notificationTypeDTO2);
        notificationTypeDTO2.setId("id2");
        assertThat(notificationTypeDTO1).isNotEqualTo(notificationTypeDTO2);
        notificationTypeDTO1.setId(null);
        assertThat(notificationTypeDTO1).isNotEqualTo(notificationTypeDTO2);
    }
}
