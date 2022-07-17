package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.CancelDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.Cancel}.
 */
public interface CancelService {
    /**
     * Save a cancel.
     *
     * @param cancelDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CancelDTO> save(CancelDTO cancelDTO);

    /**
     * Updates a cancel.
     *
     * @param cancelDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CancelDTO> update(CancelDTO cancelDTO);

    /**
     * Partially updates a cancel.
     *
     * @param cancelDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CancelDTO> partialUpdate(CancelDTO cancelDTO);

    /**
     * Get all the cancels.
     *
     * @return the list of entities.
     */
    Flux<CancelDTO> findAll();

    /**
     * Returns the number of cancels available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of cancels available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" cancel.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CancelDTO> findOne(String id);

    /**
     * Delete the "id" cancel.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the cancel corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<CancelDTO> search(String query);
}
