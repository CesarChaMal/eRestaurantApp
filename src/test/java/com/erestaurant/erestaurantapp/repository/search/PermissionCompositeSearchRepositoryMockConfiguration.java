package com.erestaurant.erestaurantapp.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link PermissionCompositeSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PermissionCompositeSearchRepositoryMockConfiguration {

    @MockBean
    private PermissionCompositeSearchRepository mockPermissionCompositeSearchRepository;
}
