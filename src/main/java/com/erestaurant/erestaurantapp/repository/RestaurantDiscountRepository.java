package com.erestaurant.erestaurantapp.repository;

import com.erestaurant.erestaurantapp.domain.RestaurantDiscount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the RestaurantDiscount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RestaurantDiscountRepository
    extends ReactiveCrudRepository<RestaurantDiscount, String>, RestaurantDiscountRepositoryInternal {
    @Query("SELECT * FROM restaurant_discount entity WHERE entity.products_id = :id")
    Flux<RestaurantDiscount> findByProducts(String id);

    @Query("SELECT * FROM restaurant_discount entity WHERE entity.products_id IS NULL")
    Flux<RestaurantDiscount> findAllWhereProductsIsNull();

    @Override
    <S extends RestaurantDiscount> Mono<S> save(S entity);

    @Override
    Flux<RestaurantDiscount> findAll();

    @Override
    Mono<RestaurantDiscount> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface RestaurantDiscountRepositoryInternal {
    <S extends RestaurantDiscount> Mono<S> save(S entity);

    Flux<RestaurantDiscount> findAllBy(Pageable pageable);

    Flux<RestaurantDiscount> findAll();

    Mono<RestaurantDiscount> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<RestaurantDiscount> findAllBy(Pageable pageable, Criteria criteria);

}
