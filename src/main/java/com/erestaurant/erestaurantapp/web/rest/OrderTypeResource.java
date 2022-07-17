package com.erestaurant.erestaurantapp.web.rest;

import com.erestaurant.erestaurantapp.repository.OrderTypeRepository;
import com.erestaurant.erestaurantapp.service.OrderTypeService;
import com.erestaurant.erestaurantapp.service.dto.OrderTypeDTO;
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
 * REST controller for managing {@link com.erestaurant.erestaurantapp.domain.OrderType}.
 */
@RestController
@RequestMapping("/api")
public class OrderTypeResource {

    private final Logger log = LoggerFactory.getLogger(OrderTypeResource.class);

    private static final String ENTITY_NAME = "orderType";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderTypeService orderTypeService;

    private final OrderTypeRepository orderTypeRepository;

    public OrderTypeResource(OrderTypeService orderTypeService, OrderTypeRepository orderTypeRepository) {
        this.orderTypeService = orderTypeService;
        this.orderTypeRepository = orderTypeRepository;
    }

    /**
     * {@code POST  /order-types} : Create a new orderType.
     *
     * @param orderTypeDTO the orderTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderTypeDTO, or with status {@code 400 (Bad Request)} if the orderType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/order-types")
    public Mono<ResponseEntity<OrderTypeDTO>> createOrderType(@Valid @RequestBody OrderTypeDTO orderTypeDTO) throws URISyntaxException {
        log.debug("REST request to save OrderType : {}", orderTypeDTO);
        if (orderTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return orderTypeService
            .save(orderTypeDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/order-types/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /order-types/:id} : Updates an existing orderType.
     *
     * @param id the id of the orderTypeDTO to save.
     * @param orderTypeDTO the orderTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderTypeDTO,
     * or with status {@code 400 (Bad Request)} if the orderTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/order-types/{id}")
    public Mono<ResponseEntity<OrderTypeDTO>> updateOrderType(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody OrderTypeDTO orderTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OrderType : {}, {}", id, orderTypeDTO);
        if (orderTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return orderTypeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return orderTypeService
                    .update(orderTypeDTO)
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
     * {@code PATCH  /order-types/:id} : Partial updates given fields of an existing orderType, field will ignore if it is null
     *
     * @param id the id of the orderTypeDTO to save.
     * @param orderTypeDTO the orderTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderTypeDTO,
     * or with status {@code 400 (Bad Request)} if the orderTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/order-types/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OrderTypeDTO>> partialUpdateOrderType(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody OrderTypeDTO orderTypeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrderType partially : {}, {}", id, orderTypeDTO);
        if (orderTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return orderTypeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OrderTypeDTO> result = orderTypeService.partialUpdate(orderTypeDTO);

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
     * {@code GET  /order-types} : get all the orderTypes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderTypes in body.
     */
    @GetMapping("/order-types")
    public Mono<List<OrderTypeDTO>> getAllOrderTypes() {
        log.debug("REST request to get all OrderTypes");
        return orderTypeService.findAll().collectList();
    }

    /**
     * {@code GET  /order-types} : get all the orderTypes as a stream.
     * @return the {@link Flux} of orderTypes.
     */
    @GetMapping(value = "/order-types", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<OrderTypeDTO> getAllOrderTypesAsStream() {
        log.debug("REST request to get all OrderTypes as a stream");
        return orderTypeService.findAll();
    }

    /**
     * {@code GET  /order-types/:id} : get the "id" orderType.
     *
     * @param id the id of the orderTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/order-types/{id}")
    public Mono<ResponseEntity<OrderTypeDTO>> getOrderType(@PathVariable String id) {
        log.debug("REST request to get OrderType : {}", id);
        Mono<OrderTypeDTO> orderTypeDTO = orderTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderTypeDTO);
    }

    /**
     * {@code DELETE  /order-types/:id} : delete the "id" orderType.
     *
     * @param id the id of the orderTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/order-types/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteOrderType(@PathVariable String id) {
        log.debug("REST request to delete OrderType : {}", id);
        return orderTypeService
            .delete(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }

    /**
     * {@code SEARCH  /_search/order-types?query=:query} : search for the orderType corresponding
     * to the query.
     *
     * @param query the query of the orderType search.
     * @return the result of the search.
     */
    @GetMapping("/_search/order-types")
    public Mono<List<OrderTypeDTO>> searchOrderTypes(@RequestParam String query) {
        log.debug("REST request to search OrderTypes for query {}", query);
        return orderTypeService.search(query).collectList();
    }
}
