package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.AppAdDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.AppAd}.
 */
public interface AppAdService {
    /**
     * Save a appAd.
     *
     * @param appAdDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<AppAdDTO> save(AppAdDTO appAdDTO);

    /**
     * Updates a appAd.
     *
     * @param appAdDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<AppAdDTO> update(AppAdDTO appAdDTO);

    /**
     * Partially updates a appAd.
     *
     * @param appAdDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<AppAdDTO> partialUpdate(AppAdDTO appAdDTO);

    /**
     * Get all the appAds.
     *
     * @return the list of entities.
     */
    Flux<AppAdDTO> findAll();

    /**
     * Returns the number of appAds available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of appAds available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" appAd.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<AppAdDTO> findOne(String id);

    /**
     * Delete the "id" appAd.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the appAd corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<AppAdDTO> search(String query);
}
