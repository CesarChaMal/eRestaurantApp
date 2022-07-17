package com.erestaurant.erestaurantapp.repository;

import com.erestaurant.erestaurantapp.domain.State;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the State entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StateRepository extends ReactiveCrudRepository<State, String>, StateRepositoryInternal {
    @Override
    <S extends State> Mono<S> save(S entity);

    @Override
    Flux<State> findAll();

    @Override
    Mono<State> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface StateRepositoryInternal {
    <S extends State> Mono<S> save(S entity);

    Flux<State> findAllBy(Pageable pageable);

    Flux<State> findAll();

    Mono<State> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<State> findAllBy(Pageable pageable, Criteria criteria);

}
