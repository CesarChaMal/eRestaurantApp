package com.erestaurant.erestaurantapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.erestaurant.erestaurantapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PermissionCompositeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PermissionComposite.class);
        PermissionComposite permissionComposite1 = new PermissionComposite();
        permissionComposite1.setId("id1");
        PermissionComposite permissionComposite2 = new PermissionComposite();
        permissionComposite2.setId(permissionComposite1.getId());
        assertThat(permissionComposite1).isEqualTo(permissionComposite2);
        permissionComposite2.setId("id2");
        assertThat(permissionComposite1).isNotEqualTo(permissionComposite2);
        permissionComposite1.setId(null);
        assertThat(permissionComposite1).isNotEqualTo(permissionComposite2);
    }
}
