package com.erestaurant.erestaurantapp.repository;

import com.erestaurant.erestaurantapp.domain.OrderType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the OrderType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderTypeRepository extends ReactiveCrudRepository<OrderType, String>, OrderTypeRepositoryInternal {
    @Override
    <S extends OrderType> Mono<S> save(S entity);

    @Override
    Flux<OrderType> findAll();

    @Override
    Mono<OrderType> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface OrderTypeRepositoryInternal {
    <S extends OrderType> Mono<S> save(S entity);

    Flux<OrderType> findAllBy(Pageable pageable);

    Flux<OrderType> findAll();

    Mono<OrderType> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<OrderType> findAllBy(Pageable pageable, Criteria criteria);

}
