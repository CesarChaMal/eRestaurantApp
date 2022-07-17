package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.RestaurantDiscount;
import com.erestaurant.erestaurantapp.repository.RestaurantDiscountRepository;
import com.erestaurant.erestaurantapp.repository.search.RestaurantDiscountSearchRepository;
import com.erestaurant.erestaurantapp.service.RestaurantDiscountService;
import com.erestaurant.erestaurantapp.service.dto.RestaurantDiscountDTO;
import com.erestaurant.erestaurantapp.service.mapper.RestaurantDiscountMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link RestaurantDiscount}.
 */
@Service
@Transactional
public class RestaurantDiscountServiceImpl implements RestaurantDiscountService {

    private final Logger log = LoggerFactory.getLogger(RestaurantDiscountServiceImpl.class);

    private final RestaurantDiscountRepository restaurantDiscountRepository;

    private final RestaurantDiscountMapper restaurantDiscountMapper;

    private final RestaurantDiscountSearchRepository restaurantDiscountSearchRepository;

    public RestaurantDiscountServiceImpl(
        RestaurantDiscountRepository restaurantDiscountRepository,
        RestaurantDiscountMapper restaurantDiscountMapper,
        RestaurantDiscountSearchRepository restaurantDiscountSearchRepository
    ) {
        this.restaurantDiscountRepository = restaurantDiscountRepository;
        this.restaurantDiscountMapper = restaurantDiscountMapper;
        this.restaurantDiscountSearchRepository = restaurantDiscountSearchRepository;
    }

    @Override
    public Mono<RestaurantDiscountDTO> save(RestaurantDiscountDTO restaurantDiscountDTO) {
        log.debug("Request to save RestaurantDiscount : {}", restaurantDiscountDTO);
        return restaurantDiscountRepository
            .save(restaurantDiscountMapper.toEntity(restaurantDiscountDTO))
            .flatMap(restaurantDiscountSearchRepository::save)
            .map(restaurantDiscountMapper::toDto);
    }

    @Override
    public Mono<RestaurantDiscountDTO> update(RestaurantDiscountDTO restaurantDiscountDTO) {
        log.debug("Request to save RestaurantDiscount : {}", restaurantDiscountDTO);
        return restaurantDiscountRepository
            .save(restaurantDiscountMapper.toEntity(restaurantDiscountDTO).setIsPersisted())
            .flatMap(restaurantDiscountSearchRepository::save)
            .map(restaurantDiscountMapper::toDto);
    }

    @Override
    public Mono<RestaurantDiscountDTO> partialUpdate(RestaurantDiscountDTO restaurantDiscountDTO) {
        log.debug("Request to partially update RestaurantDiscount : {}", restaurantDiscountDTO);

        return restaurantDiscountRepository
            .findById(restaurantDiscountDTO.getId())
            .map(existingRestaurantDiscount -> {
                restaurantDiscountMapper.partialUpdate(existingRestaurantDiscount, restaurantDiscountDTO);

                return existingRestaurantDiscount;
            })
            .flatMap(restaurantDiscountRepository::save)
            .flatMap(savedRestaurantDiscount -> {
                restaurantDiscountSearchRepository.save(savedRestaurantDiscount);

                return Mono.just(savedRestaurantDiscount);
            })
            .map(restaurantDiscountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RestaurantDiscountDTO> findAll() {
        log.debug("Request to get all RestaurantDiscounts");
        return restaurantDiscountRepository.findAll().map(restaurantDiscountMapper::toDto);
    }

    public Mono<Long> countAll() {
        return restaurantDiscountRepository.count();
    }

    public Mono<Long> searchCount() {
        return restaurantDiscountSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RestaurantDiscountDTO> findOne(String id) {
        log.debug("Request to get RestaurantDiscount : {}", id);
        return restaurantDiscountRepository.findById(id).map(restaurantDiscountMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete RestaurantDiscount : {}", id);
        return restaurantDiscountRepository.deleteById(id).then(restaurantDiscountSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RestaurantDiscountDTO> search(String query) {
        log.debug("Request to search RestaurantDiscounts for query {}", query);
        return restaurantDiscountSearchRepository.search(query).map(restaurantDiscountMapper::toDto);
    }
}
