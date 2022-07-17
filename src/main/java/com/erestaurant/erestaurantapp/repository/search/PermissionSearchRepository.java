package com.erestaurant.erestaurantapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.erestaurant.erestaurantapp.domain.Permission;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Permission} entity.
 */
public interface PermissionSearchRepository
    extends ReactiveElasticsearchRepository<Permission, String>, PermissionSearchRepositoryInternal {}

interface PermissionSearchRepositoryInternal {
    Flux<Permission> search(String query);
}

class PermissionSearchRepositoryInternalImpl implements PermissionSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    PermissionSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Permission> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, Permission.class).map(SearchHit::getContent);
    }
}
