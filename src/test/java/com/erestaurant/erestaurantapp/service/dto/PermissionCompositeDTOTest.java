package com.erestaurant.erestaurantapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.erestaurantapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PermissionCompositeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PermissionCompositeDTO.class);
        PermissionCompositeDTO permissionCompositeDTO1 = new PermissionCompositeDTO();
        permissionCompositeDTO1.setId("id1");
        PermissionCompositeDTO permissionCompositeDTO2 = new PermissionCompositeDTO();
        assertThat(permissionCompositeDTO1).isNotEqualTo(permissionCompositeDTO2);
        permissionCompositeDTO2.setId(permissionCompositeDTO1.getId());
        assertThat(permissionCompositeDTO1).isEqualTo(permissionCompositeDTO2);
        permissionCompositeDTO2.setId("id2");
        assertThat(permissionCompositeDTO1).isNotEqualTo(permissionCompositeDTO2);
        permissionCompositeDTO1.setId(null);
        assertThat(permissionCompositeDTO1).isNotEqualTo(permissionCompositeDTO2);
    }
}
