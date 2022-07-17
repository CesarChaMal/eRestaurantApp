package com.erestaurant.erestaurantapp.repository;

import com.erestaurant.erestaurantapp.domain.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends ReactiveCrudRepository<Notification, String>, NotificationRepositoryInternal {
    @Query("SELECT * FROM notification entity WHERE entity.type_id = :id")
    Flux<Notification> findByType(String id);

    @Query("SELECT * FROM notification entity WHERE entity.type_id IS NULL")
    Flux<Notification> findAllWhereTypeIsNull();

    @Query("SELECT * FROM notification entity WHERE entity.restaurant_id = :id")
    Flux<Notification> findByRestaurant(String id);

    @Query("SELECT * FROM notification entity WHERE entity.restaurant_id IS NULL")
    Flux<Notification> findAllWhereRestaurantIsNull();

    @Override
    <S extends Notification> Mono<S> save(S entity);

    @Override
    Flux<Notification> findAll();

    @Override
    Mono<Notification> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface NotificationRepositoryInternal {
    <S extends Notification> Mono<S> save(S entity);

    Flux<Notification> findAllBy(Pageable pageable);

    Flux<Notification> findAll();

    Mono<Notification> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Notification> findAllBy(Pageable pageable, Criteria criteria);

}
