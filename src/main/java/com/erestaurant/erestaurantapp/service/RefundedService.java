package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.RefundedDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.Refunded}.
 */
public interface RefundedService {
    /**
     * Save a refunded.
     *
     * @param refundedDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<RefundedDTO> save(RefundedDTO refundedDTO);

    /**
     * Updates a refunded.
     *
     * @param refundedDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<RefundedDTO> update(RefundedDTO refundedDTO);

    /**
     * Partially updates a refunded.
     *
     * @param refundedDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RefundedDTO> partialUpdate(RefundedDTO refundedDTO);

    /**
     * Get all the refundeds.
     *
     * @return the list of entities.
     */
    Flux<RefundedDTO> findAll();

    /**
     * Returns the number of refundeds available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of refundeds available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" refunded.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RefundedDTO> findOne(String id);

    /**
     * Delete the "id" refunded.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the refunded corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<RefundedDTO> search(String query);
}
