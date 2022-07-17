package com.erestaurant.erestaurantapp.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.erestaurant.erestaurantapp.domain.Categories;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Categories} entity.
 */
public interface CategoriesSearchRepository
    extends ReactiveElasticsearchRepository<Categories, String>, CategoriesSearchRepositoryInternal {}

interface CategoriesSearchRepositoryInternal {
    Flux<Categories> search(String query);
}

class CategoriesSearchRepositoryInternalImpl implements CategoriesSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    CategoriesSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Categories> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return reactiveElasticsearchTemplate.search(nativeSearchQuery, Categories.class).map(SearchHit::getContent);
    }
}
