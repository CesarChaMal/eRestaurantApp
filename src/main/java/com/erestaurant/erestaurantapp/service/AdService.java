package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.AdDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.Ad}.
 */
public interface AdService {
    /**
     * Save a ad.
     *
     * @param adDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<AdDTO> save(AdDTO adDTO);

    /**
     * Updates a ad.
     *
     * @param adDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<AdDTO> update(AdDTO adDTO);

    /**
     * Partially updates a ad.
     *
     * @param adDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<AdDTO> partialUpdate(AdDTO adDTO);

    /**
     * Get all the ads.
     *
     * @return the list of entities.
     */
    Flux<AdDTO> findAll();

    /**
     * Returns the number of ads available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of ads available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" ad.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<AdDTO> findOne(String id);

    /**
     * Delete the "id" ad.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the ad corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<AdDTO> search(String query);
}
