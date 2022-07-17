package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.OrderTypeDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.OrderType}.
 */
public interface OrderTypeService {
    /**
     * Save a orderType.
     *
     * @param orderTypeDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<OrderTypeDTO> save(OrderTypeDTO orderTypeDTO);

    /**
     * Updates a orderType.
     *
     * @param orderTypeDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<OrderTypeDTO> update(OrderTypeDTO orderTypeDTO);

    /**
     * Partially updates a orderType.
     *
     * @param orderTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<OrderTypeDTO> partialUpdate(OrderTypeDTO orderTypeDTO);

    /**
     * Get all the orderTypes.
     *
     * @return the list of entities.
     */
    Flux<OrderTypeDTO> findAll();

    /**
     * Returns the number of orderTypes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of orderTypes available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" orderType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<OrderTypeDTO> findOne(String id);

    /**
     * Delete the "id" orderType.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the orderType corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<OrderTypeDTO> search(String query);
}
