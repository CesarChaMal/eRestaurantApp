package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.NotificationTypeDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.NotificationType}.
 */
public interface NotificationTypeService {
    /**
     * Save a notificationType.
     *
     * @param notificationTypeDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<NotificationTypeDTO> save(NotificationTypeDTO notificationTypeDTO);

    /**
     * Updates a notificationType.
     *
     * @param notificationTypeDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<NotificationTypeDTO> update(NotificationTypeDTO notificationTypeDTO);

    /**
     * Partially updates a notificationType.
     *
     * @param notificationTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<NotificationTypeDTO> partialUpdate(NotificationTypeDTO notificationTypeDTO);

    /**
     * Get all the notificationTypes.
     *
     * @return the list of entities.
     */
    Flux<NotificationTypeDTO> findAll();

    /**
     * Returns the number of notificationTypes available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of notificationTypes available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" notificationType.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<NotificationTypeDTO> findOne(String id);

    /**
     * Delete the "id" notificationType.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the notificationType corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<NotificationTypeDTO> search(String query);
}
