package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.CompleteDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.Complete}.
 */
public interface CompleteService {
    /**
     * Save a complete.
     *
     * @param completeDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CompleteDTO> save(CompleteDTO completeDTO);

    /**
     * Updates a complete.
     *
     * @param completeDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CompleteDTO> update(CompleteDTO completeDTO);

    /**
     * Partially updates a complete.
     *
     * @param completeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CompleteDTO> partialUpdate(CompleteDTO completeDTO);

    /**
     * Get all the completes.
     *
     * @return the list of entities.
     */
    Flux<CompleteDTO> findAll();

    /**
     * Returns the number of completes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of completes available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" complete.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CompleteDTO> findOne(String id);

    /**
     * Delete the "id" complete.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the complete corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<CompleteDTO> search(String query);
}
