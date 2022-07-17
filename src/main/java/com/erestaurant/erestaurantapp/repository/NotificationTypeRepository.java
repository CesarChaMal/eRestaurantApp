package com.erestaurant.erestaurantapp.repository;

import com.erestaurant.erestaurantapp.domain.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the NotificationType entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationTypeRepository extends ReactiveCrudRepository<NotificationType, String>, NotificationTypeRepositoryInternal {
    @Override
    <S extends NotificationType> Mono<S> save(S entity);

    @Override
    Flux<NotificationType> findAll();

    @Override
    Mono<NotificationType> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface NotificationTypeRepositoryInternal {
    <S extends NotificationType> Mono<S> save(S entity);

    Flux<NotificationType> findAllBy(Pageable pageable);

    Flux<NotificationType> findAll();

    Mono<NotificationType> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<NotificationType> findAllBy(Pageable pageable, Criteria criteria);

}
