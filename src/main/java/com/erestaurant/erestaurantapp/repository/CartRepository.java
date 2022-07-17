package com.erestaurant.erestaurantapp.repository;

import com.erestaurant.erestaurantapp.domain.Cart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Cart entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartRepository extends ReactiveCrudRepository<Cart, String>, CartRepositoryInternal {
    @Query("SELECT * FROM cart entity WHERE entity.order_id = :id")
    Flux<Cart> findByOrder(String id);

    @Query("SELECT * FROM cart entity WHERE entity.order_id IS NULL")
    Flux<Cart> findAllWhereOrderIsNull();

    @Override
    <S extends Cart> Mono<S> save(S entity);

    @Override
    Flux<Cart> findAll();

    @Override
    Mono<Cart> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface CartRepositoryInternal {
    <S extends Cart> Mono<S> save(S entity);

    Flux<Cart> findAllBy(Pageable pageable);

    Flux<Cart> findAll();

    Mono<Cart> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Cart> findAllBy(Pageable pageable, Criteria criteria);

}
