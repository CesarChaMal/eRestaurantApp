package com.erestaurant.erestaurantapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.erestaurant.erestaurantapp.domain.PermissionComposite;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link PermissionComposite} entity.
 */
public interface PermissionCompositeSearchRepository
    extends ReactiveElasticsearchRepository<PermissionComposite, String>, PermissionCompositeSearchRepositoryInternal {}

interface PermissionCompositeSearchRepositoryInternal {
    Flux<PermissionComposite> search(String query);
}

class PermissionCompositeSearchRepositoryInternalImpl implements PermissionCompositeSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    PermissionCompositeSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<PermissionComposite> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, PermissionComposite.class).map(SearchHit::getContent);
    }
}
