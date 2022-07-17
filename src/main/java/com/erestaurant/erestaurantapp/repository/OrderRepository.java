package com.erestaurant.erestaurantapp.repository;

import com.erestaurant.erestaurantapp.domain.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Order entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, String>, OrderRepositoryInternal {
    @Query("SELECT * FROM jhi_order entity WHERE entity.type_id = :id")
    Flux<Order> findByType(String id);

    @Query("SELECT * FROM jhi_order entity WHERE entity.type_id IS NULL")
    Flux<Order> findAllWhereTypeIsNull();

    @Query("SELECT * FROM jhi_order entity WHERE entity.state_id = :id")
    Flux<Order> findByState(String id);

    @Query("SELECT * FROM jhi_order entity WHERE entity.state_id IS NULL")
    Flux<Order> findAllWhereStateIsNull();

    @Query("SELECT * FROM jhi_order entity WHERE entity.customer_id = :id")
    Flux<Order> findByCustomer(String id);

    @Query("SELECT * FROM jhi_order entity WHERE entity.customer_id IS NULL")
    Flux<Order> findAllWhereCustomerIsNull();

    @Override
    <S extends Order> Mono<S> save(S entity);

    @Override
    Flux<Order> findAll();

    @Override
    Mono<Order> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface OrderRepositoryInternal {
    <S extends Order> Mono<S> save(S entity);

    Flux<Order> findAllBy(Pageable pageable);

    Flux<Order> findAll();

    Mono<Order> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Order> findAllBy(Pageable pageable, Criteria criteria);

}
