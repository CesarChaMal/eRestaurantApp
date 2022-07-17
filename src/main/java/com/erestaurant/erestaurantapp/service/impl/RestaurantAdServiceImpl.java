package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.RestaurantAd;
import com.erestaurant.erestaurantapp.repository.RestaurantAdRepository;
import com.erestaurant.erestaurantapp.repository.search.RestaurantAdSearchRepository;
import com.erestaurant.erestaurantapp.service.RestaurantAdService;
import com.erestaurant.erestaurantapp.service.dto.RestaurantAdDTO;
import com.erestaurant.erestaurantapp.service.mapper.RestaurantAdMapper;
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
 * Service Implementation for managing {@link RestaurantAd}.
 */
@Service
@Transactional
public class RestaurantAdServiceImpl implements RestaurantAdService {

    private final Logger log = LoggerFactory.getLogger(RestaurantAdServiceImpl.class);

    private final RestaurantAdRepository restaurantAdRepository;

    private final RestaurantAdMapper restaurantAdMapper;

    private final RestaurantAdSearchRepository restaurantAdSearchRepository;

    public RestaurantAdServiceImpl(
        RestaurantAdRepository restaurantAdRepository,
        RestaurantAdMapper restaurantAdMapper,
        RestaurantAdSearchRepository restaurantAdSearchRepository
    ) {
        this.restaurantAdRepository = restaurantAdRepository;
        this.restaurantAdMapper = restaurantAdMapper;
        this.restaurantAdSearchRepository = restaurantAdSearchRepository;
    }

    @Override
    public Mono<RestaurantAdDTO> save(RestaurantAdDTO restaurantAdDTO) {
        log.debug("Request to save RestaurantAd : {}", restaurantAdDTO);
        return restaurantAdRepository
            .save(restaurantAdMapper.toEntity(restaurantAdDTO))
            .flatMap(restaurantAdSearchRepository::save)
            .map(restaurantAdMapper::toDto);
    }

    @Override
    public Mono<RestaurantAdDTO> update(RestaurantAdDTO restaurantAdDTO) {
        log.debug("Request to save RestaurantAd : {}", restaurantAdDTO);
        return restaurantAdRepository
            .save(restaurantAdMapper.toEntity(restaurantAdDTO).setIsPersisted())
            .flatMap(restaurantAdSearchRepository::save)
            .map(restaurantAdMapper::toDto);
    }

    @Override
    public Mono<RestaurantAdDTO> partialUpdate(RestaurantAdDTO restaurantAdDTO) {
        log.debug("Request to partially update RestaurantAd : {}", restaurantAdDTO);

        return restaurantAdRepository
            .findById(restaurantAdDTO.getId())
            .map(existingRestaurantAd -> {
                restaurantAdMapper.partialUpdate(existingRestaurantAd, restaurantAdDTO);

                return existingRestaurantAd;
            })
            .flatMap(restaurantAdRepository::save)
            .flatMap(savedRestaurantAd -> {
                restaurantAdSearchRepository.save(savedRestaurantAd);

                return Mono.just(savedRestaurantAd);
            })
            .map(restaurantAdMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RestaurantAdDTO> findAll() {
        log.debug("Request to get all RestaurantAds");
        return restaurantAdRepository.findAll().map(restaurantAdMapper::toDto);
    }

    public Mono<Long> countAll() {
        return restaurantAdRepository.count();
    }

    public Mono<Long> searchCount() {
        return restaurantAdSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RestaurantAdDTO> findOne(String id) {
        log.debug("Request to get RestaurantAd : {}", id);
        return restaurantAdRepository.findById(id).map(restaurantAdMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete RestaurantAd : {}", id);
        return restaurantAdRepository.deleteById(id).then(restaurantAdSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RestaurantAdDTO> search(String query) {
        log.debug("Request to search RestaurantAds for query {}", query);
        return restaurantAdSearchRepository.search(query).map(restaurantAdMapper::toDto);
    }
}
