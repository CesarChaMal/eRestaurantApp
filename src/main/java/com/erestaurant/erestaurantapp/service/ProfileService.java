package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.ProfileDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.Profile}.
 */
public interface ProfileService {
    /**
     * Save a profile.
     *
     * @param profileDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ProfileDTO> save(ProfileDTO profileDTO);

    /**
     * Updates a profile.
     *
     * @param profileDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ProfileDTO> update(ProfileDTO profileDTO);

    /**
     * Partially updates a profile.
     *
     * @param profileDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ProfileDTO> partialUpdate(ProfileDTO profileDTO);

    /**
     * Get all the profiles.
     *
     * @return the list of entities.
     */
    Flux<ProfileDTO> findAll();

    /**
     * Returns the number of profiles available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of profiles available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" profile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProfileDTO> findOne(String id);

    /**
     * Delete the "id" profile.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the profile corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<ProfileDTO> search(String query);
}
