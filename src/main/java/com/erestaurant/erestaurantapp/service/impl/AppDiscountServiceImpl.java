package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.AppDiscount;
import com.erestaurant.erestaurantapp.repository.AppDiscountRepository;
import com.erestaurant.erestaurantapp.repository.search.AppDiscountSearchRepository;
import com.erestaurant.erestaurantapp.service.AppDiscountService;
import com.erestaurant.erestaurantapp.service.dto.AppDiscountDTO;
import com.erestaurant.erestaurantapp.service.mapper.AppDiscountMapper;
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
 * Service Implementation for managing {@link AppDiscount}.
 */
@Service
@Transactional
public class AppDiscountServiceImpl implements AppDiscountService {

    private final Logger log = LoggerFactory.getLogger(AppDiscountServiceImpl.class);

    private final AppDiscountRepository appDiscountRepository;

    private final AppDiscountMapper appDiscountMapper;

    private final AppDiscountSearchRepository appDiscountSearchRepository;

    public AppDiscountServiceImpl(
        AppDiscountRepository appDiscountRepository,
        AppDiscountMapper appDiscountMapper,
        AppDiscountSearchRepository appDiscountSearchRepository
    ) {
        this.appDiscountRepository = appDiscountRepository;
        this.appDiscountMapper = appDiscountMapper;
        this.appDiscountSearchRepository = appDiscountSearchRepository;
    }

    @Override
    public Mono<AppDiscountDTO> save(AppDiscountDTO appDiscountDTO) {
        log.debug("Request to save AppDiscount : {}", appDiscountDTO);
        return appDiscountRepository
            .save(appDiscountMapper.toEntity(appDiscountDTO))
            .flatMap(appDiscountSearchRepository::save)
            .map(appDiscountMapper::toDto);
    }

    @Override
    public Mono<AppDiscountDTO> update(AppDiscountDTO appDiscountDTO) {
        log.debug("Request to save AppDiscount : {}", appDiscountDTO);
        return appDiscountRepository
            .save(appDiscountMapper.toEntity(appDiscountDTO).setIsPersisted())
            .flatMap(appDiscountSearchRepository::save)
            .map(appDiscountMapper::toDto);
    }

    @Override
    public Mono<AppDiscountDTO> partialUpdate(AppDiscountDTO appDiscountDTO) {
        log.debug("Request to partially update AppDiscount : {}", appDiscountDTO);

        return appDiscountRepository
            .findById(appDiscountDTO.getId())
            .map(existingAppDiscount -> {
                appDiscountMapper.partialUpdate(existingAppDiscount, appDiscountDTO);

                return existingAppDiscount;
            })
            .flatMap(appDiscountRepository::save)
            .flatMap(savedAppDiscount -> {
                appDiscountSearchRepository.save(savedAppDiscount);

                return Mono.just(savedAppDiscount);
            })
            .map(appDiscountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AppDiscountDTO> findAll() {
        log.debug("Request to get all AppDiscounts");
        return appDiscountRepository.findAll().map(appDiscountMapper::toDto);
    }

    public Mono<Long> countAll() {
        return appDiscountRepository.count();
    }

    public Mono<Long> searchCount() {
        return appDiscountSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AppDiscountDTO> findOne(String id) {
        log.debug("Request to get AppDiscount : {}", id);
        return appDiscountRepository.findById(id).map(appDiscountMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete AppDiscount : {}", id);
        return appDiscountRepository.deleteById(id).then(appDiscountSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AppDiscountDTO> search(String query) {
        log.debug("Request to search AppDiscounts for query {}", query);
        return appDiscountSearchRepository.search(query).map(appDiscountMapper::toDto);
    }
}
