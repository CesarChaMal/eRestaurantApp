package com.erestaurant.erestaurantapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.erestaurant.erestaurantapp.domain.Cart;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Cart} entity.
 */
public interface CartSearchRepository extends ReactiveElasticsearchRepository<Cart, String>, CartSearchRepositoryInternal {}

interface CartSearchRepositoryInternal {
    Flux<Cart> search(String query);
}

class CartSearchRepositoryInternalImpl implements CartSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    CartSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Cart> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, Cart.class).map(SearchHit::getContent);
    }
}
