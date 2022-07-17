package com.erestaurant.erestaurantapp.web.rest;

import com.erestaurant.erestaurantapp.repository.AppAdRepository;
import com.erestaurant.erestaurantapp.service.AppAdService;
import com.erestaurant.erestaurantapp.service.dto.AppAdDTO;
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
 * REST controller for managing {@link com.erestaurant.erestaurantapp.domain.AppAd}.
 */
@RestController
@RequestMapping("/api")
public class AppAdResource {

    private final Logger log = LoggerFactory.getLogger(AppAdResource.class);

    private static final String ENTITY_NAME = "appAd";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppAdService appAdService;

    private final AppAdRepository appAdRepository;

    public AppAdResource(AppAdService appAdService, AppAdRepository appAdRepository) {
        this.appAdService = appAdService;
        this.appAdRepository = appAdRepository;
    }

    /**
     * {@code POST  /app-ads} : Create a new appAd.
     *
     * @param appAdDTO the appAdDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appAdDTO, or with status {@code 400 (Bad Request)} if the appAd has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/app-ads")
    public Mono<ResponseEntity<AppAdDTO>> createAppAd(@Valid @RequestBody AppAdDTO appAdDTO) throws URISyntaxException {
        log.debug("REST request to save AppAd : {}", appAdDTO);
        if (appAdDTO.getId() != null) {
            throw new BadRequestAlertException("A new appAd cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return appAdService
            .save(appAdDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/app-ads/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /app-ads/:id} : Updates an existing appAd.
     *
     * @param id the id of the appAdDTO to save.
     * @param appAdDTO the appAdDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appAdDTO,
     * or with status {@code 400 (Bad Request)} if the appAdDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appAdDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/app-ads/{id}")
    public Mono<ResponseEntity<AppAdDTO>> updateAppAd(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody AppAdDTO appAdDTO
    ) throws URISyntaxException {
        log.debug("REST request to update AppAd : {}, {}", id, appAdDTO);
        if (appAdDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appAdDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return appAdRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return appAdService
                    .update(appAdDTO)
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
     * {@code PATCH  /app-ads/:id} : Partial updates given fields of an existing appAd, field will ignore if it is null
     *
     * @param id the id of the appAdDTO to save.
     * @param appAdDTO the appAdDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appAdDTO,
     * or with status {@code 400 (Bad Request)} if the appAdDTO is not valid,
     * or with status {@code 404 (Not Found)} if the appAdDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the appAdDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/app-ads/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AppAdDTO>> partialUpdateAppAd(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody AppAdDTO appAdDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update AppAd partially : {}, {}", id, appAdDTO);
        if (appAdDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appAdDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return appAdRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<AppAdDTO> result = appAdService.partialUpdate(appAdDTO);

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
     * {@code GET  /app-ads} : get all the appAds.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appAds in body.
     */
    @GetMapping("/app-ads")
    public Mono<List<AppAdDTO>> getAllAppAds() {
        log.debug("REST request to get all AppAds");
        return appAdService.findAll().collectList();
    }

    /**
     * {@code GET  /app-ads} : get all the appAds as a stream.
     * @return the {@link Flux} of appAds.
     */
    @GetMapping(value = "/app-ads", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<AppAdDTO> getAllAppAdsAsStream() {
        log.debug("REST request to get all AppAds as a stream");
        return appAdService.findAll();
    }

    /**
     * {@code GET  /app-ads/:id} : get the "id" appAd.
     *
     * @param id the id of the appAdDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appAdDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/app-ads/{id}")
    public Mono<ResponseEntity<AppAdDTO>> getAppAd(@PathVariable String id) {
        log.debug("REST request to get AppAd : {}", id);
        Mono<AppAdDTO> appAdDTO = appAdService.findOne(id);
        return ResponseUtil.wrapOrNotFound(appAdDTO);
    }

    /**
     * {@code DELETE  /app-ads/:id} : delete the "id" appAd.
     *
     * @param id the id of the appAdDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/app-ads/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteAppAd(@PathVariable String id) {
        log.debug("REST request to delete AppAd : {}", id);
        return appAdService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }

    /**
     * {@code SEARCH  /_search/app-ads?query=:query} : search for the appAd corresponding
     * to the query.
     *
     * @param query the query of the appAd search.
     * @return the result of the search.
     */
    @GetMapping("/_search/app-ads")
    public Mono<List<AppAdDTO>> searchAppAds(@RequestParam String query) {
        log.debug("REST request to search AppAds for query {}", query);
        return appAdService.search(query).collectList();
    }
}
