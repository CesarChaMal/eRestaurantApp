package com.erestaurant.erestaurantapp.repository;

import com.erestaurant.erestaurantapp.domain.AppDiscount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the AppDiscount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppDiscountRepository extends ReactiveCrudRepository<AppDiscount, String>, AppDiscountRepositoryInternal {
    @Query("SELECT * FROM app_discount entity WHERE entity.cart_id = :id")
    Flux<AppDiscount> findByCart(String id);

    @Query("SELECT * FROM app_discount entity WHERE entity.cart_id IS NULL")
    Flux<AppDiscount> findAllWhereCartIsNull();

    @Override
    <S extends AppDiscount> Mono<S> save(S entity);

    @Override
    Flux<AppDiscount> findAll();

    @Override
    Mono<AppDiscount> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface AppDiscountRepositoryInternal {
    <S extends AppDiscount> Mono<S> save(S entity);

    Flux<AppDiscount> findAllBy(Pageable pageable);

    Flux<AppDiscount> findAll();

    Mono<AppDiscount> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<AppDiscount> findAllBy(Pageable pageable, Criteria criteria);

}
