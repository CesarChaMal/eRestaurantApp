package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.NewOrderDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.NewOrder}.
 */
public interface NewOrderService {
    /**
     * Save a newOrder.
     *
     * @param newOrderDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<NewOrderDTO> save(NewOrderDTO newOrderDTO);

    /**
     * Updates a newOrder.
     *
     * @param newOrderDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<NewOrderDTO> update(NewOrderDTO newOrderDTO);

    /**
     * Partially updates a newOrder.
     *
     * @param newOrderDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<NewOrderDTO> partialUpdate(NewOrderDTO newOrderDTO);

    /**
     * Get all the newOrders.
     *
     * @return the list of entities.
     */
    Flux<NewOrderDTO> findAll();

    /**
     * Returns the number of newOrders available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of newOrders available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" newOrder.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<NewOrderDTO> findOne(String id);

    /**
     * Delete the "id" newOrder.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the newOrder corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<NewOrderDTO> search(String query);
}
