package com.erestaurant.erestaurantapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.erestaurant.erestaurantapp.domain.RestaurantAd;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link RestaurantAd} entity.
 */
public interface RestaurantAdSearchRepository
    extends ReactiveElasticsearchRepository<RestaurantAd, String>, RestaurantAdSearchRepositoryInternal {}

interface RestaurantAdSearchRepositoryInternal {
    Flux<RestaurantAd> search(String query);
}

class RestaurantAdSearchRepositoryInternalImpl implements RestaurantAdSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    RestaurantAdSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<RestaurantAd> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, RestaurantAd.class).map(SearchHit::getContent);
    }
}
