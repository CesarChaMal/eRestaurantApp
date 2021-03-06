package com.erestaurant.erestaurantapp.repository;

import com.erestaurant.erestaurantapp.domain.CustomerProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the CustomerProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerProfileRepository extends ReactiveCrudRepository<CustomerProfile, String>, CustomerProfileRepositoryInternal {
    @Override
    <S extends CustomerProfile> Mono<S> save(S entity);

    @Override
    Flux<CustomerProfile> findAll();

    @Override
    Mono<CustomerProfile> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface CustomerProfileRepositoryInternal {
    <S extends CustomerProfile> Mono<S> save(S entity);

    Flux<CustomerProfile> findAllBy(Pageable pageable);

    Flux<CustomerProfile> findAll();

    Mono<CustomerProfile> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<CustomerProfile> findAllBy(Pageable pageable, Criteria criteria);

}
