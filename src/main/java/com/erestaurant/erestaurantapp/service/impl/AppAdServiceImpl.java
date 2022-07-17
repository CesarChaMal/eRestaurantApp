package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.AppAd;
import com.erestaurant.erestaurantapp.repository.AppAdRepository;
import com.erestaurant.erestaurantapp.repository.search.AppAdSearchRepository;
import com.erestaurant.erestaurantapp.service.AppAdService;
import com.erestaurant.erestaurantapp.service.dto.AppAdDTO;
import com.erestaurant.erestaurantapp.service.mapper.AppAdMapper;
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
 * Service Implementation for managing {@link AppAd}.
 */
@Service
@Transactional
public class AppAdServiceImpl implements AppAdService {

    private final Logger log = LoggerFactory.getLogger(AppAdServiceImpl.class);

    private final AppAdRepository appAdRepository;

    private final AppAdMapper appAdMapper;

    private final AppAdSearchRepository appAdSearchRepository;

    public AppAdServiceImpl(AppAdRepository appAdRepository, AppAdMapper appAdMapper, AppAdSearchRepository appAdSearchRepository) {
        this.appAdRepository = appAdRepository;
        this.appAdMapper = appAdMapper;
        this.appAdSearchRepository = appAdSearchRepository;
    }

    @Override
    public Mono<AppAdDTO> save(AppAdDTO appAdDTO) {
        log.debug("Request to save AppAd : {}", appAdDTO);
        return appAdRepository.save(appAdMapper.toEntity(appAdDTO)).flatMap(appAdSearchRepository::save).map(appAdMapper::toDto);
    }

    @Override
    public Mono<AppAdDTO> update(AppAdDTO appAdDTO) {
        log.debug("Request to save AppAd : {}", appAdDTO);
        return appAdRepository
            .save(appAdMapper.toEntity(appAdDTO).setIsPersisted())
            .flatMap(appAdSearchRepository::save)
            .map(appAdMapper::toDto);
    }

    @Override
    public Mono<AppAdDTO> partialUpdate(AppAdDTO appAdDTO) {
        log.debug("Request to partially update AppAd : {}", appAdDTO);

        return appAdRepository
            .findById(appAdDTO.getId())
            .map(existingAppAd -> {
                appAdMapper.partialUpdate(existingAppAd, appAdDTO);

                return existingAppAd;
            })
            .flatMap(appAdRepository::save)
            .flatMap(savedAppAd -> {
                appAdSearchRepository.save(savedAppAd);

                return Mono.just(savedAppAd);
            })
            .map(appAdMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AppAdDTO> findAll() {
        log.debug("Request to get all AppAds");
        return appAdRepository.findAll().map(appAdMapper::toDto);
    }

    public Mono<Long> countAll() {
        return appAdRepository.count();
    }

    public Mono<Long> searchCount() {
        return appAdSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AppAdDTO> findOne(String id) {
        log.debug("Request to get AppAd : {}", id);
        return appAdRepository.findById(id).map(appAdMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete AppAd : {}", id);
        return appAdRepository.deleteById(id).then(appAdSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AppAdDTO> search(String query) {
        log.debug("Request to search AppAds for query {}", query);
        return appAdSearchRepository.search(query).map(appAdMapper::toDto);
    }
}
