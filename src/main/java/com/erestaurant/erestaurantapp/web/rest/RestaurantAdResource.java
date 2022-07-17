package com.erestaurant.erestaurantapp.web.rest;

import com.erestaurant.erestaurantapp.repository.RestaurantAdRepository;
import com.erestaurant.erestaurantapp.service.RestaurantAdService;
import com.erestaurant.erestaurantapp.service.dto.RestaurantAdDTO;
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
 * REST controller for managing {@link com.erestaurant.erestaurantapp.domain.RestaurantAd}.
 */
@RestController
@RequestMapping("/api")
public class RestaurantAdResource {

    private final Logger log = LoggerFactory.getLogger(RestaurantAdResource.class);

    private static final String ENTITY_NAME = "restaurantAd";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RestaurantAdService restaurantAdService;

    private final RestaurantAdRepository restaurantAdRepository;

    public RestaurantAdResource(RestaurantAdService restaurantAdService, RestaurantAdRepository restaurantAdRepository) {
        this.restaurantAdService = restaurantAdService;
        this.restaurantAdRepository = restaurantAdRepository;
    }

    /**
     * {@code POST  /restaurant-ads} : Create a new restaurantAd.
     *
     * @param restaurantAdDTO the restaurantAdDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new restaurantAdDTO, or with status {@code 400 (Bad Request)} if the restaurantAd has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/restaurant-ads")
    public Mono<ResponseEntity<RestaurantAdDTO>> createRestaurantAd(@Valid @RequestBody RestaurantAdDTO restaurantAdDTO)
        throws URISyntaxException {
        log.debug("REST request to save RestaurantAd : {}", restaurantAdDTO);
        if (restaurantAdDTO.getId() != null) {
            throw new BadRequestAlertException("A new restaurantAd cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return restaurantAdService
            .save(restaurantAdDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/restaurant-ads/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /restaurant-ads/:id} : Updates an existing restaurantAd.
     *
     * @param id the id of the restaurantAdDTO to save.
     * @param restaurantAdDTO the restaurantAdDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurantAdDTO,
     * or with status {@code 400 (Bad Request)} if the restaurantAdDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the restaurantAdDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/restaurant-ads/{id}")
    public Mono<ResponseEntity<RestaurantAdDTO>> updateRestaurantAd(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody RestaurantAdDTO restaurantAdDTO
    ) throws URISyntaxException {
        log.debug("REST request to update RestaurantAd : {}, {}", id, restaurantAdDTO);
        if (restaurantAdDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurantAdDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return restaurantAdRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return restaurantAdService
                    .update(restaurantAdDTO)
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
     * {@code PATCH  /restaurant-ads/:id} : Partial updates given fields of an existing restaurantAd, field will ignore if it is null
     *
     * @param id the id of the restaurantAdDTO to save.
     * @param restaurantAdDTO the restaurantAdDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurantAdDTO,
     * or with status {@code 400 (Bad Request)} if the restaurantAdDTO is not valid,
     * or with status {@code 404 (Not Found)} if the restaurantAdDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the restaurantAdDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/restaurant-ads/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RestaurantAdDTO>> partialUpdateRestaurantAd(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody RestaurantAdDTO restaurantAdDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update RestaurantAd partially : {}, {}", id, restaurantAdDTO);
        if (restaurantAdDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurantAdDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return restaurantAdRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<RestaurantAdDTO> result = restaurantAdService.partialUpdate(restaurantAdDTO);

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
     * {@code GET  /restaurant-ads} : get all the restaurantAds.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of restaurantAds in body.
     */
    @GetMapping("/restaurant-ads")
    public Mono<List<RestaurantAdDTO>> getAllRestaurantAds() {
        log.debug("REST request to get all RestaurantAds");
        return restaurantAdService.findAll().collectList();
    }

    /**
     * {@code GET  /restaurant-ads} : get all the restaurantAds as a stream.
     * @return the {@link Flux} of restaurantAds.
     */
    @GetMapping(value = "/restaurant-ads", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<RestaurantAdDTO> getAllRestaurantAdsAsStream() {
        log.debug("REST request to get all RestaurantAds as a stream");
        return restaurantAdService.findAll();
    }

    /**
     * {@code GET  /restaurant-ads/:id} : get the "id" restaurantAd.
     *
     * @param id the id of the restaurantAdDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the restaurantAdDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/restaurant-ads/{id}")
    public Mono<ResponseEntity<RestaurantAdDTO>> getRestaurantAd(@PathVariable String id) {
        log.debug("REST request to get RestaurantAd : {}", id);
        Mono<RestaurantAdDTO> restaurantAdDTO = restaurantAdService.findOne(id);
        return ResponseUtil.wrapOrNotFound(restaurantAdDTO);
    }

    /**
     * {@code DELETE  /restaurant-ads/:id} : delete the "id" restaurantAd.
     *
     * @param id the id of the restaurantAdDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/restaurant-ads/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRestaurantAd(@PathVariable String id) {
        log.debug("REST request to delete RestaurantAd : {}", id);
        return restaurantAdService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }

    /**
     * {@code SEARCH  /_search/restaurant-ads?query=:query} : search for the restaurantAd corresponding
     * to the query.
     *
     * @param query the query of the restaurantAd search.
     * @return the result of the search.
     */
    @GetMapping("/_search/restaurant-ads")
    public Mono<List<RestaurantAdDTO>> searchRestaurantAds(@RequestParam String query) {
        log.debug("REST request to search RestaurantAds for query {}", query);
        return restaurantAdService.search(query).collectList();
    }
}
