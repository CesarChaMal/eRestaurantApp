package com.erestaurant.erestaurantapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.erestaurant.erestaurantapp.domain.Refunded;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Refunded} entity.
 */
public interface RefundedSearchRepository extends ReactiveElasticsearchRepository<Refunded, String>, RefundedSearchRepositoryInternal {}

interface RefundedSearchRepositoryInternal {
    Flux<Refunded> search(String query);
}

class RefundedSearchRepositoryInternalImpl implements RefundedSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    RefundedSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Refunded> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, Refunded.class).map(SearchHit::getContent);
    }
}
