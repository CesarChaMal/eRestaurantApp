package com.erestaurant.erestaurantapp.web.rest;

import com.erestaurant.erestaurantapp.repository.NotificationTypeRepository;
import com.erestaurant.erestaurantapp.service.NotificationTypeService;
import com.erestaurant.erestaurantapp.service.dto.NotificationTypeDTO;
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
 * REST controller for managing {@link com.erestaurant.erestaurantapp.domain.NotificationType}.
 */
@RestController
@RequestMapping("/api")
public class NotificationTypeResource {

    private final Logger log = LoggerFactory.getLogger(NotificationTypeResource.class);

    private static final String ENTITY_NAME = "notificationType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotificationTypeService notificationTypeService;

    private final NotificationTypeRepository notificationTypeRepository;

    public NotificationTypeResource(
        NotificationTypeService notificationTypeService,
        NotificationTypeRepository notificationTypeRepository
    ) {
        this.notificationTypeService = notificationTypeService;
        this.notificationTypeRepository = notificationTypeRepository;
    }

    /**
     * {@code POST  /notification-types} : Create a new notificationType.
     *
     * @param notificationTypeDTO the notificationTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificationTypeDTO, or with status {@code 400 (Bad Request)} if the notificationType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/notification-types")
    public Mono<ResponseEntity<NotificationTypeDTO>> createNotificationType(@Valid @RequestBody NotificationTypeDTO notificationTypeDTO)
        throws URISyntaxException {
        log.debug("REST request to save NotificationType : {}", notificationTypeDTO);
        if (notificationTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new notificationType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return notificationTypeService
            .save(notificationTypeDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/notification-types/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /notification-types/:id} : Updates an existing notificationType.
     *
     * @param id the id of the notificationTypeDTO to save.
     * @param notificationTypeDTO the notificationTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationTypeDTO,
     * or with status {@code 400 (Bad Request)} if the notificationTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/notification-types/{id}")
    public Mono<ResponseEntity<NotificationTypeDTO>> updateNotificationType(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody NotificationTypeDTO notificationTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update NotificationType : {}, {}", id, notificationTypeDTO);
        if (notificationTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return notificationTypeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return notificationTypeService
                    .update(notificationTypeDTO)
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
     * {@code PATCH  /notification-types/:id} : Partial updates given fields of an existing notificationType, field will ignore if it is null
     *
     * @param id the id of the notificationTypeDTO to save.
     * @param notificationTypeDTO the notificationTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationTypeDTO,
     * or with status {@code 400 (Bad Request)} if the notificationTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the notificationTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the notificationTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/notification-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<NotificationTypeDTO>> partialUpdateNotificationType(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody NotificationTypeDTO notificationTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update NotificationType partially : {}, {}", id, notificationTypeDTO);
        if (notificationTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return notificationTypeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<NotificationTypeDTO> result = notificationTypeService.partialUpdate(notificationTypeDTO);

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
     * {@code GET  /notification-types} : get all the notificationTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notificationTypes in body.
     */
    @GetMapping("/notification-types")
    public Mono<List<NotificationTypeDTO>> getAllNotificationTypes() {
        log.debug("REST request to get all NotificationTypes");
        return notificationTypeService.findAll().collectList();
    }

    /**
     * {@code GET  /notification-types} : get all the notificationTypes as a stream.
     * @return the {@link Flux} of notificationTypes.
     */
    @GetMapping(value = "/notification-types", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<NotificationTypeDTO> getAllNotificationTypesAsStream() {
        log.debug("REST request to get all NotificationTypes as a stream");
        return notificationTypeService.findAll();
    }

    /**
     * {@code GET  /notification-types/:id} : get the "id" notificationType.
     *
     * @param id the id of the notificationTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/notification-types/{id}")
    public Mono<ResponseEntity<NotificationTypeDTO>> getNotificationType(@PathVariable String id) {
        log.debug("REST request to get NotificationType : {}", id);
        Mono<NotificationTypeDTO> notificationTypeDTO = notificationTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notificationTypeDTO);
    }

    /**
     * {@code DELETE  /notification-types/:id} : delete the "id" notificationType.
     *
     * @param id the id of the notificationTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/notification-types/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteNotificationType(@PathVariable String id) {
        log.debug("REST request to delete NotificationType : {}", id);
        return notificationTypeService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }

    /**
     * {@code SEARCH  /_search/notification-types?query=:query} : search for the notificationType corresponding
     * to the query.
     *
     * @param query the query of the notificationType search.
     * @return the result of the search.
     */
    @GetMapping("/_search/notification-types")
    public Mono<List<NotificationTypeDTO>> searchNotificationTypes(@RequestParam String query) {
        log.debug("REST request to search NotificationTypes for query {}", query);
        return notificationTypeService.search(query).collectList();
    }
}
