package com.erestaurant.erestaurantapp.repository;

import com.erestaurant.erestaurantapp.domain.PermissionComposite;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the PermissionComposite entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PermissionCompositeRepository
    extends ReactiveCrudRepository<PermissionComposite, String>, PermissionCompositeRepositoryInternal {
    @Query("SELECT * FROM permission_composite entity WHERE entity.permission_id = :id")
    Flux<PermissionComposite> findByPermission(String id);

    @Query("SELECT * FROM permission_composite entity WHERE entity.permission_id IS NULL")
    Flux<PermissionComposite> findAllWherePermissionIsNull();

    @Override
    <S extends PermissionComposite> Mono<S> save(S entity);

    @Override
    Flux<PermissionComposite> findAll();

    @Override
    Mono<PermissionComposite> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface PermissionCompositeRepositoryInternal {
    <S extends PermissionComposite> Mono<S> save(S entity);

    Flux<PermissionComposite> findAllBy(Pageable pageable);

    Flux<PermissionComposite> findAll();

    Mono<PermissionComposite> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<PermissionComposite> findAllBy(Pageable pageable, Criteria criteria);

}
