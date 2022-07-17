package com.erestaurant.erestaurantapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.erestaurant.erestaurantapp.domain.RestaurantDiscount;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link RestaurantDiscount} entity.
 */
public interface RestaurantDiscountSearchRepository
    extends ReactiveElasticsearchRepository<RestaurantDiscount, String>, RestaurantDiscountSearchRepositoryInternal {}

interface RestaurantDiscountSearchRepositoryInternal {
    Flux<RestaurantDiscount> search(String query);
}

class RestaurantDiscountSearchRepositoryInternalImpl implements RestaurantDiscountSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    RestaurantDiscountSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<RestaurantDiscount> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, RestaurantDiscount.class).map(SearchHit::getContent);
    }
}
