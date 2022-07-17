package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.StateDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.State}.
 */
public interface StateService {
    /**
     * Save a state.
     *
     * @param stateDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<StateDTO> save(StateDTO stateDTO);

    /**
     * Updates a state.
     *
     * @param stateDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<StateDTO> update(StateDTO stateDTO);

    /**
     * Partially updates a state.
     *
     * @param stateDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<StateDTO> partialUpdate(StateDTO stateDTO);

    /**
     * Get all the states.
     *
     * @return the list of entities.
     */
    Flux<StateDTO> findAll();

    /**
     * Returns the number of states available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of states available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" state.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<StateDTO> findOne(String id);

    /**
     * Delete the "id" state.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the state corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<StateDTO> search(String query);
}
