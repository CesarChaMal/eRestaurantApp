package com.erestaurant.erestaurantapp.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link OrderSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class OrderSearchRepositoryMockConfiguration {

    @MockBean
    private OrderSearchRepository mockOrderSearchRepository;
}
