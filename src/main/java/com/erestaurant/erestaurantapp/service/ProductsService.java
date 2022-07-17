package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.ProductsDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.Products}.
 */
public interface ProductsService {
    /**
     * Save a products.
     *
     * @param productsDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ProductsDTO> save(ProductsDTO productsDTO);

    /**
     * Updates a products.
     *
     * @param productsDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ProductsDTO> update(ProductsDTO productsDTO);

    /**
     * Partially updates a products.
     *
     * @param productsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ProductsDTO> partialUpdate(ProductsDTO productsDTO);

    /**
     * Get all the products.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProductsDTO> findAll(Pageable pageable);

    /**
     * Returns the number of products available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of products available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" products.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProductsDTO> findOne(String id);

    /**
     * Delete the "id" products.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the products corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProductsDTO> search(String query, Pageable pageable);
}
