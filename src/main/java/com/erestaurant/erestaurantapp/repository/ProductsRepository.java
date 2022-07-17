package com.erestaurant.erestaurantapp.repository;

import com.erestaurant.erestaurantapp.domain.Products;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Products entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductsRepository extends ReactiveCrudRepository<Products, String>, ProductsRepositoryInternal {
    Flux<Products> findAllBy(Pageable pageable);

    @Query("SELECT * FROM products entity WHERE entity.category_id = :id")
    Flux<Products> findByCategory(String id);

    @Query("SELECT * FROM products entity WHERE entity.category_id IS NULL")
    Flux<Products> findAllWhereCategoryIsNull();

    @Query("SELECT * FROM products entity WHERE entity.restaurant_id = :id")
    Flux<Products> findByRestaurant(String id);

    @Query("SELECT * FROM products entity WHERE entity.restaurant_id IS NULL")
    Flux<Products> findAllWhereRestaurantIsNull();

    @Query("SELECT * FROM products entity WHERE entity.cart_id = :id")
    Flux<Products> findByCart(String id);

    @Query("SELECT * FROM products entity WHERE entity.cart_id IS NULL")
    Flux<Products> findAllWhereCartIsNull();

    @Override
    <S extends Products> Mono<S> save(S entity);

    @Override
    Flux<Products> findAll();

    @Override
    Mono<Products> findById(String id);

    @Override
    Mono<Void> deleteById(String id);
}

interface ProductsRepositoryInternal {
    <S extends Products> Mono<S> save(S entity);

    Flux<Products> findAllBy(Pageable pageable);

    Flux<Products> findAll();

    Mono<Products> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Products> findAllBy(Pageable pageable, Criteria criteria);

}
