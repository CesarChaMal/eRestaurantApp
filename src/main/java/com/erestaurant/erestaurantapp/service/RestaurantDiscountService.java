package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.RestaurantDiscountDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.RestaurantDiscount}.
 */
public interface RestaurantDiscountService {
    /**
     * Save a restaurantDiscount.
     *
     * @param restaurantDiscountDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<RestaurantDiscountDTO> save(RestaurantDiscountDTO restaurantDiscountDTO);

    /**
     * Updates a restaurantDiscount.
     *
     * @param restaurantDiscountDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<RestaurantDiscountDTO> update(RestaurantDiscountDTO restaurantDiscountDTO);

    /**
     * Partially updates a restaurantDiscount.
     *
     * @param restaurantDiscountDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RestaurantDiscountDTO> partialUpdate(RestaurantDiscountDTO restaurantDiscountDTO);

    /**
     * Get all the restaurantDiscounts.
     *
     * @return the list of entities.
     */
    Flux<RestaurantDiscountDTO> findAll();

    /**
     * Returns the number of restaurantDiscounts available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of restaurantDiscounts available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" restaurantDiscount.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RestaurantDiscountDTO> findOne(String id);

    /**
     * Delete the "id" restaurantDiscount.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the restaurantDiscount corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<RestaurantDiscountDTO> search(String query);
}
