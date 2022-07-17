package com.erestaurant.erestaurantapp.service;

import com.erestaurant.erestaurantapp.service.dto.RoleDTO;
import java.util.List;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.erestaurant.erestaurantapp.domain.Role}.
 */
public interface RoleService {
    /**
     * Save a role.
     *
     * @param roleDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<RoleDTO> save(RoleDTO roleDTO);

    /**
     * Updates a role.
     *
     * @param roleDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<RoleDTO> update(RoleDTO roleDTO);

    /**
     * Partially updates a role.
     *
     * @param roleDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RoleDTO> partialUpdate(RoleDTO roleDTO);

    /**
     * Get all the roles.
     *
     * @return the list of entities.
     */
    Flux<RoleDTO> findAll();

    /**
     * Get all the roles with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RoleDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of roles available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Returns the number of roles available in search repository.
     *
     */
    Mono<Long> searchCount();

    /**
     * Get the "id" role.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RoleDTO> findOne(String id);

    /**
     * Delete the "id" role.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Search for the role corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    Flux<RoleDTO> search(String query);
}
