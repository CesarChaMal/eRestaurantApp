package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Restaurant;
import com.erestaurant.erestaurantapp.repository.RestaurantRepository;
import com.erestaurant.erestaurantapp.repository.search.RestaurantSearchRepository;
import com.erestaurant.erestaurantapp.service.RestaurantService;
import com.erestaurant.erestaurantapp.service.dto.RestaurantDTO;
import com.erestaurant.erestaurantapp.service.mapper.RestaurantMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Restaurant}.
 */
@Service
@Transactional
public class RestaurantServiceImpl implements RestaurantService {

    private final Logger log = LoggerFactory.getLogger(RestaurantServiceImpl.class);

    private final RestaurantRepository restaurantRepository;

    private final RestaurantMapper restaurantMapper;

    private final RestaurantSearchRepository restaurantSearchRepository;

    public RestaurantServiceImpl(
        RestaurantRepository restaurantRepository,
        RestaurantMapper restaurantMapper,
        RestaurantSearchRepository restaurantSearchRepository
    ) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
        this.restaurantSearchRepository = restaurantSearchRepository;
    }

    @Override
    public Mono<RestaurantDTO> save(RestaurantDTO restaurantDTO) {
        log.debug("Request to save Restaurant : {}", restaurantDTO);
        return restaurantRepository
            .save(restaurantMapper.toEntity(restaurantDTO))
            .flatMap(restaurantSearchRepository::save)
            .map(restaurantMapper::toDto);
    }

    @Override
    public Mono<RestaurantDTO> update(RestaurantDTO restaurantDTO) {
        log.debug("Request to save Restaurant : {}", restaurantDTO);
        return restaurantRepository
            .save(restaurantMapper.toEntity(restaurantDTO).setIsPersisted())
            .flatMap(restaurantSearchRepository::save)
            .map(restaurantMapper::toDto);
    }

    @Override
    public Mono<RestaurantDTO> partialUpdate(RestaurantDTO restaurantDTO) {
        log.debug("Request to partially update Restaurant : {}", restaurantDTO);

        return restaurantRepository
            .findById(restaurantDTO.getId())
            .map(existingRestaurant -> {
                restaurantMapper.partialUpdate(existingRestaurant, restaurantDTO);

                return existingRestaurant;
            })
            .flatMap(restaurantRepository::save)
            .flatMap(savedRestaurant -> {
                restaurantSearchRepository.save(savedRestaurant);

                return Mono.just(savedRestaurant);
            })
            .map(restaurantMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RestaurantDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Restaurants");
        return restaurantRepository.findAllBy(pageable).map(restaurantMapper::toDto);
    }

    public Mono<Long> countAll() {
        return restaurantRepository.count();
    }

    public Mono<Long> searchCount() {
        return restaurantSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RestaurantDTO> findOne(String id) {
        log.debug("Request to get Restaurant : {}", id);
        return restaurantRepository.findById(id).map(restaurantMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Restaurant : {}", id);
        return restaurantRepository.deleteById(id).then(restaurantSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RestaurantDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Restaurants for query {}", query);
        return restaurantSearchRepository.search(query, pageable).map(restaurantMapper::toDto);
    }
}
