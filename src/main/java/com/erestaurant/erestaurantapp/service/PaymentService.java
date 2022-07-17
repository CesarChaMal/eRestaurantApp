package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.PaymentDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.Payment}.
 */
public interface PaymentService {
    /**
     * Save a payment.
     *
     * @param paymentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PaymentDTO> save(PaymentDTO paymentDTO);

    /**
     * Updates a payment.
     *
     * @param paymentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<PaymentDTO> update(PaymentDTO paymentDTO);

    /**
     * Partially updates a payment.
     *
     * @param paymentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PaymentDTO> partialUpdate(PaymentDTO paymentDTO);

    /**
     * Get all the payments.
     *
     * @return the list of entities.
     */
    Flux<PaymentDTO> findAll();

    /**
     * Returns the number of payments available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of payments available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" payment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PaymentDTO> findOne(String id);

    /**
     * Delete the "id" payment.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the payment corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<PaymentDTO> search(String query);
}
