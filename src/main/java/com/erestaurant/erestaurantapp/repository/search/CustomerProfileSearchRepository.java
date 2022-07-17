package com.erestaurant.erestaurantapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.erestaurant.erestaurantapp.domain.CustomerProfile;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link CustomerProfile} entity.
 */
public interface CustomerProfileSearchRepository
    extends ReactiveElasticsearchRepository<CustomerProfile, String>, CustomerProfileSearchRepositoryInternal {}

interface CustomerProfileSearchRepositoryInternal {
    Flux<CustomerProfile> search(String query);
}

class CustomerProfileSearchRepositoryInternalImpl implements CustomerProfileSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    CustomerProfileSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<CustomerProfile> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, CustomerProfile.class).map(SearchHit::getContent);
    }
}
