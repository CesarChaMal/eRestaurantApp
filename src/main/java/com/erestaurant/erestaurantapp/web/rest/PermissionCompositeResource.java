package com.erestaurant.erestaurantapp.web.rest;

import com.erestaurant.erestaurantapp.repository.PermissionCompositeRepository;
import com.erestaurant.erestaurantapp.service.PermissionCompositeService;
import com.erestaurant.erestaurantapp.service.dto.PermissionCompositeDTO;
import com.erestaurant.erestaurantapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.erestaurant.erestaurantapp.domain.PermissionComposite}.
 */
@RestController
@RequestMapping("/api")
public class PermissionCompositeResource {

    private final Logger log = LoggerFactory.getLogger(PermissionCompositeResource.class);

    private static final String ENTITY_NAME = "permissionComposite";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PermissionCompositeService permissionCompositeService;

    private final PermissionCompositeRepository permissionCompositeRepository;

    public PermissionCompositeResource(
        PermissionCompositeService permissionCompositeService,
        PermissionCompositeRepository permissionCompositeRepository
    ) {
        this.permissionCompositeService = permissionCompositeService;
        this.permissionCompositeRepository = permissionCompositeRepository;
    }

    /**
     * {@code POST  /permission-composites} : Create a new permissionComposite.
     *
     * @param permissionCompositeDTO the permissionCompositeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new permissionCompositeDTO, or with status {@code 400 (Bad Request)} if the permissionComposite has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/permission-composites")
    public Mono<ResponseEntity<PermissionCompositeDTO>> createPermissionComposite(
        @Valid @RequestBody PermissionCompositeDTO permissionCompositeDTO
    ) throws URISyntaxException {
        log.debug("REST request to save PermissionComposite : {}", permissionCompositeDTO);
        if (permissionCompositeDTO.getId() != null) {
            throw new BadRequestAlertException("A new permissionComposite cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return permissionCompositeService
            .save(permissionCompositeDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/permission-composites/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /permission-composites/:id} : Updates an existing permissionComposite.
     *
     * @param id the id of the permissionCompositeDTO to save.
     * @param permissionCompositeDTO the permissionCompositeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated permissionCompositeDTO,
     * or with status {@code 400 (Bad Request)} if the permissionCompositeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the permissionCompositeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/permission-composites/{id}")
    public Mono<ResponseEntity<PermissionCompositeDTO>> updatePermissionComposite(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody PermissionCompositeDTO permissionCompositeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update PermissionComposite : {}, {}", id, permissionCompositeDTO);
        if (permissionCompositeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, permissionCompositeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return permissionCompositeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return permissionCompositeService
                    .update(permissionCompositeDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /permission-composites/:id} : Partial updates given fields of an existing permissionComposite, field will ignore if it is null
     *
     * @param id the id of the permissionCompositeDTO to save.
     * @param permissionCompositeDTO the permissionCompositeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated permissionCompositeDTO,
     * or with status {@code 400 (Bad Request)} if the permissionCompositeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the permissionCompositeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the permissionCompositeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/permission-composites/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PermissionCompositeDTO>> partialUpdatePermissionComposite(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody PermissionCompositeDTO permissionCompositeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update PermissionComposite partially : {}, {}", id, permissionCompositeDTO);
        if (permissionCompositeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, permissionCompositeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return permissionCompositeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PermissionCompositeDTO> result = permissionCompositeService.partialUpdate(permissionCompositeDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /permission-composites} : get all the permissionComposites.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of permissionComposites in body.
     */
    @GetMapping("/permission-composites")
    public Mono<List<PermissionCompositeDTO>> getAllPermissionComposites() {
        log.debug("REST request to get all PermissionComposites");
        return permissionCompositeService.findAll().collectList();
    }

    /**
     * {@code GET  /permission-composites} : get all the permissionComposites as a stream.
     * @return the {@link Flux} of permissionComposites.
     */
    @GetMapping(value = "/permission-composites", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PermissionCompositeDTO> getAllPermissionCompositesAsStream() {
        log.debug("REST request to get all PermissionComposites as a stream");
        return permissionCompositeService.findAll();
    }

    /**
     * {@code GET  /permission-composites/:id} : get the "id" permissionComposite.
     *
     * @param id the id of the permissionCompositeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the permissionCompositeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/permission-composites/{id}")
    public Mono<ResponseEntity<PermissionCompositeDTO>> getPermissionComposite(@PathVariable String id) {
        log.debug("REST request to get PermissionComposite : {}", id);
        Mono<PermissionCompositeDTO> permissionCompositeDTO = permissionCompositeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(permissionCompositeDTO);
    }

    /**
     * {@code DELETE  /permission-composites/:id} : delete the "id" permissionComposite.
     *
     * @param id the id of the permissionCompositeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/permission-composites/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePermissionComposite(@PathVariable String id) {
        log.debug("REST request to delete PermissionComposite : {}", id);
        return permissionCompositeService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }

    /**
     * {@code SEARCH  /_search/permission-composites?query=:query} : search for the permissionComposite corresponding
     * to the query.
     *
     * @param query the query of the permissionComposite search.
     * @return the result of the search.
     */
    @GetMapping("/_search/permission-composites")
    public Mono<List<PermissionCompositeDTO>> searchPermissionComposites(@RequestParam String query) {
        log.debug("REST request to search PermissionComposites for query {}", query);
        return permissionCompositeService.search(query).collectList();
    }
}
