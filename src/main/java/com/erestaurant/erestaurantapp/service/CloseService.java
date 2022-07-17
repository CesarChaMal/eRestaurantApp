package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.CloseDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.Close}.
 */
public interface CloseService {
    /**
     * Save a close.
     *
     * @param closeDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CloseDTO> save(CloseDTO closeDTO);

    /**
     * Updates a close.
     *
     * @param closeDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CloseDTO> update(CloseDTO closeDTO);

    /**
     * Partially updates a close.
     *
     * @param closeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CloseDTO> partialUpdate(CloseDTO closeDTO);

    /**
     * Get all the closes.
     *
     * @return the list of entities.
     */
    Flux<CloseDTO> findAll();

    /**
     * Returns the number of closes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of closes available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" close.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CloseDTO> findOne(String id);

    /**
     * Delete the "id" close.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the close corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<CloseDTO> search(String query);
}
