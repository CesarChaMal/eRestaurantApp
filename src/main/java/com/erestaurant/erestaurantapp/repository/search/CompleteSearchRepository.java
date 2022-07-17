package com.erestaurant.erestaurantapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.erestaurant.erestaurantapp.domain.Complete;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Complete} entity.
 */
public interface CompleteSearchRepository extends ReactiveElasticsearchRepository<Complete, String>, CompleteSearchRepositoryInternal {}

interface CompleteSearchRepositoryInternal {
    Flux<Complete> search(String query);
}

class CompleteSearchRepositoryInternalImpl implements CompleteSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    CompleteSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Complete> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, Complete.class).map(SearchHit::getContent);
    }
}
