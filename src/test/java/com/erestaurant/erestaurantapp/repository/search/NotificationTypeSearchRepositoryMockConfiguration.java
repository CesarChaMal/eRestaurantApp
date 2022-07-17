package com.erestaurant.erestaurantapp.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link NotificationTypeSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class NotificationTypeSearchRepositoryMockConfiguration {

    @MockBean
    private NotificationTypeSearchRepository mockNotificationTypeSearchRepository;
}
