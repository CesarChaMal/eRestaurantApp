package com.erestaurant.erestaurantapp.repository;

import com.erestaurant.erestaurantapp.domain.AppAd;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the AppAd entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AppAdRepository extends ReactiveCrudRepository<AppAd, String>, AppAdRepositoryInternal {
    @Query("SELECT * FROM app_ad entity WHERE entity.admin_id = :id")
    Flux<AppAd> findByAdmin(String id);

    @Query("SELECT * FROM app_ad entity WHERE entity.admin_id IS NULL")
    Flux<AppAd> findAllWhereAdminIsNull();

    @Override
    <S extends AppAd> Mono<S> save(S entity);

    @Override
    Flux<AppAd> findAll();

    @Override
    Mono<AppAd> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface AppAdRepositoryInternal {
    <S extends AppAd> Mono<S> save(S entity);

    Flux<AppAd> findAllBy(Pageable pageable);

    Flux<AppAd> findAll();

    Mono<AppAd> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<AppAd> findAllBy(Pageable pageable, Criteria criteria);

}
