package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.PermissionDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.Permission}.
 */
public interface PermissionService {
    /**
     * Save a permission.
     *
     * @param permissionDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<PermissionDTO> save(PermissionDTO permissionDTO);

    /**
     * Updates a permission.
     *
     * @param permissionDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<PermissionDTO> update(PermissionDTO permissionDTO);

    /**
     * Partially updates a permission.
     *
     * @param permissionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<PermissionDTO> partialUpdate(PermissionDTO permissionDTO);

    /**
     * Get all the permissions.
     *
     * @return the list of entities.
     */
    Flux<PermissionDTO> findAll();

    /**
     * Returns the number of permissions available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of permissions available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" permission.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<PermissionDTO> findOne(String id);

    /**
     * Delete the "id" permission.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the permission corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<PermissionDTO> search(String query);
}
