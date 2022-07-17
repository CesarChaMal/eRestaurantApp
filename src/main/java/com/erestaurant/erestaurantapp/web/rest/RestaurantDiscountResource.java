package com.erestaurant.erestaurantapp.web.rest;

import com.erestaurant.erestaurantapp.repository.RestaurantDiscountRepository;
import com.erestaurant.erestaurantapp.service.RestaurantDiscountService;
import com.erestaurant.erestaurantapp.service.dto.RestaurantDiscountDTO;
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
 * REST controller for managing {@link com.erestaurant.erestaurantapp.domain.RestaurantDiscount}.
 */
@RestController
@RequestMapping("/api")
public class RestaurantDiscountResource {

    private final Logger log = LoggerFactory.getLogger(RestaurantDiscountResource.class);

    private static final String ENTITY_NAME = "restaurantDiscount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RestaurantDiscountService restaurantDiscountService;

    private final RestaurantDiscountRepository restaurantDiscountRepository;

    public RestaurantDiscountResource(
        RestaurantDiscountService restaurantDiscountService,
        RestaurantDiscountRepository restaurantDiscountRepository
    ) {
        this.restaurantDiscountService = restaurantDiscountService;
        this.restaurantDiscountRepository = restaurantDiscountRepository;
    }

    /**
     * {@code POST  /restaurant-discounts} : Create a new restaurantDiscount.
     *
     * @param restaurantDiscountDTO the restaurantDiscountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new restaurantDiscountDTO, or with status {@code 400 (Bad Request)} if the restaurantDiscount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/restaurant-discounts")
    public Mono<ResponseEntity<RestaurantDiscountDTO>> createRestaurantDiscount(
        @Valid @RequestBody RestaurantDiscountDTO restaurantDiscountDTO
    ) throws URISyntaxException {
        log.debug("REST request to save RestaurantDiscount : {}", restaurantDiscountDTO);
        if (restaurantDiscountDTO.getId() != null) {
            throw new BadRequestAlertException("A new restaurantDiscount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return restaurantDiscountService
            .save(restaurantDiscountDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/restaurant-discounts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /restaurant-discounts/:id} : Updates an existing restaurantDiscount.
     *
     * @param id the id of the restaurantDiscountDTO to save.
     * @param restaurantDiscountDTO the restaurantDiscountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurantDiscountDTO,
     * or with status {@code 400 (Bad Request)} if the restaurantDiscountDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the restaurantDiscountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/restaurant-discounts/{id}")
    public Mono<ResponseEntity<RestaurantDiscountDTO>> updateRestaurantDiscount(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody RestaurantDiscountDTO restaurantDiscountDTO
    ) throws URISyntaxException {
        log.debug("REST request to update RestaurantDiscount : {}, {}", id, restaurantDiscountDTO);
        if (restaurantDiscountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurantDiscountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return restaurantDiscountRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return restaurantDiscountService
                    .update(restaurantDiscountDTO)
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
     * {@code PATCH  /restaurant-discounts/:id} : Partial updates given fields of an existing restaurantDiscount, field will ignore if it is null
     *
     * @param id the id of the restaurantDiscountDTO to save.
     * @param restaurantDiscountDTO the restaurantDiscountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated restaurantDiscountDTO,
     * or with status {@code 400 (Bad Request)} if the restaurantDiscountDTO is not valid,
     * or with status {@code 404 (Not Found)} if the restaurantDiscountDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the restaurantDiscountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/restaurant-discounts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RestaurantDiscountDTO>> partialUpdateRestaurantDiscount(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody RestaurantDiscountDTO restaurantDiscountDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update RestaurantDiscount partially : {}, {}", id, restaurantDiscountDTO);
        if (restaurantDiscountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, restaurantDiscountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return restaurantDiscountRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<RestaurantDiscountDTO> result = restaurantDiscountService.partialUpdate(restaurantDiscountDTO);

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
     * {@code GET  /restaurant-discounts} : get all the restaurantDiscounts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of restaurantDiscounts in body.
     */
    @GetMapping("/restaurant-discounts")
    public Mono<List<RestaurantDiscountDTO>> getAllRestaurantDiscounts() {
        log.debug("REST request to get all RestaurantDiscounts");
        return restaurantDiscountService.findAll().collectList();
    }

    /**
     * {@code GET  /restaurant-discounts} : get all the restaurantDiscounts as a stream.
     * @return the {@link Flux} of restaurantDiscounts.
     */
    @GetMapping(value = "/restaurant-discounts", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<RestaurantDiscountDTO> getAllRestaurantDiscountsAsStream() {
        log.debug("REST request to get all RestaurantDiscounts as a stream");
        return restaurantDiscountService.findAll();
    }

    /**
     * {@code GET  /restaurant-discounts/:id} : get the "id" restaurantDiscount.
     *
     * @param id the id of the restaurantDiscountDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the restaurantDiscountDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/restaurant-discounts/{id}")
    public Mono<ResponseEntity<RestaurantDiscountDTO>> getRestaurantDiscount(@PathVariable String id) {
        log.debug("REST request to get RestaurantDiscount : {}", id);
        Mono<RestaurantDiscountDTO> restaurantDiscountDTO = restaurantDiscountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(restaurantDiscountDTO);
    }

    /**
     * {@code DELETE  /restaurant-discounts/:id} : delete the "id" restaurantDiscount.
     *
     * @param id the id of the restaurantDiscountDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/restaurant-discounts/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRestaurantDiscount(@PathVariable String id) {
        log.debug("REST request to delete RestaurantDiscount : {}", id);
        return restaurantDiscountService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }

    /**
     * {@code SEARCH  /_search/restaurant-discounts?query=:query} : search for the restaurantDiscount corresponding
     * to the query.
     *
     * @param query the query of the restaurantDiscount search.
     * @return the result of the search.
     */
    @GetMapping("/_search/restaurant-discounts")
    public Mono<List<RestaurantDiscountDTO>> searchRestaurantDiscounts(@RequestParam String query) {
        log.debug("REST request to search RestaurantDiscounts for query {}", query);
        return restaurantDiscountService.search(query).collectList();
    }
}
