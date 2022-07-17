package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Ad;
import com.erestaurant.erestaurantapp.repository.AdRepository;
import com.erestaurant.erestaurantapp.repository.search.AdSearchRepository;
import com.erestaurant.erestaurantapp.service.AdService;
import com.erestaurant.erestaurantapp.service.dto.AdDTO;
import com.erestaurant.erestaurantapp.service.mapper.AdMapper;
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
 * Service Implementation for managing {@link Ad}.
 */
@Service
@Transactional
public class AdServiceImpl implements AdService {

    private final Logger log = LoggerFactory.getLogger(AdServiceImpl.class);

    private final AdRepository adRepository;

    private final AdMapper adMapper;

    private final AdSearchRepository adSearchRepository;

    public AdServiceImpl(AdRepository adRepository, AdMapper adMapper, AdSearchRepository adSearchRepository) {
        this.adRepository = adRepository;
        this.adMapper = adMapper;
        this.adSearchRepository = adSearchRepository;
    }

    @Override
    public Mono<AdDTO> save(AdDTO adDTO) {
        log.debug("Request to save Ad : {}", adDTO);
        return adRepository.save(adMapper.toEntity(adDTO)).flatMap(adSearchRepository::save).map(adMapper::toDto);
    }

    @Override
    public Mono<AdDTO> update(AdDTO adDTO) {
        log.debug("Request to save Ad : {}", adDTO);
        return adRepository.save(adMapper.toEntity(adDTO).setIsPersisted()).flatMap(adSearchRepository::save).map(adMapper::toDto);
    }

    @Override
    public Mono<AdDTO> partialUpdate(AdDTO adDTO) {
        log.debug("Request to partially update Ad : {}", adDTO);

        return adRepository
            .findById(adDTO.getId())
            .map(existingAd -> {
                adMapper.partialUpdate(existingAd, adDTO);

                return existingAd;
            })
            .flatMap(adRepository::save)
            .flatMap(savedAd -> {
                adSearchRepository.save(savedAd);

                return Mono.just(savedAd);
            })
            .map(adMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AdDTO> findAll() {
        log.debug("Request to get all Ads");
        return adRepository.findAll().map(adMapper::toDto);
    }

    public Mono<Long> countAll() {
        return adRepository.count();
    }

    public Mono<Long> searchCount() {
        return adSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AdDTO> findOne(String id) {
        log.debug("Request to get Ad : {}", id);
        return adRepository.findById(id).map(adMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Ad : {}", id);
        return adRepository.deleteById(id).then(adSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AdDTO> search(String query) {
        log.debug("Request to search Ads for query {}", query);
        return adSearchRepository.search(query).map(adMapper::toDto);
    }
}
