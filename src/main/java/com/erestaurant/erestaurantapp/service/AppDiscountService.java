package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.AppDiscountDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.AppDiscount}.
 */
public interface AppDiscountService {
    /**
     * Save a appDiscount.
     *
     * @param appDiscountDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<AppDiscountDTO> save(AppDiscountDTO appDiscountDTO);

    /**
     * Updates a appDiscount.
     *
     * @param appDiscountDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<AppDiscountDTO> update(AppDiscountDTO appDiscountDTO);

    /**
     * Partially updates a appDiscount.
     *
     * @param appDiscountDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<AppDiscountDTO> partialUpdate(AppDiscountDTO appDiscountDTO);

    /**
     * Get all the appDiscounts.
     *
     * @return the list of entities.
     */
    Flux<AppDiscountDTO> findAll();

    /**
     * Returns the number of appDiscounts available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of appDiscounts available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" appDiscount.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<AppDiscountDTO> findOne(String id);

    /**
     * Delete the "id" appDiscount.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the appDiscount corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<AppDiscountDTO> search(String query);
}
