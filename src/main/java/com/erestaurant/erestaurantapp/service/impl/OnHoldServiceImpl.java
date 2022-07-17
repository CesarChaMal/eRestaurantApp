package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.OnHold;
import com.erestaurant.erestaurantapp.repository.OnHoldRepository;
import com.erestaurant.erestaurantapp.repository.search.OnHoldSearchRepository;
import com.erestaurant.erestaurantapp.service.OnHoldService;
import com.erestaurant.erestaurantapp.service.dto.OnHoldDTO;
import com.erestaurant.erestaurantapp.service.mapper.OnHoldMapper;
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
 * Service Implementation for managing {@link OnHold}.
 */
@Service
@Transactional
public class OnHoldServiceImpl implements OnHoldService {

    private final Logger log = LoggerFactory.getLogger(OnHoldServiceImpl.class);

    private final OnHoldRepository onHoldRepository;

    private final OnHoldMapper onHoldMapper;

    private final OnHoldSearchRepository onHoldSearchRepository;

    public OnHoldServiceImpl(OnHoldRepository onHoldRepository, OnHoldMapper onHoldMapper, OnHoldSearchRepository onHoldSearchRepository) {
        this.onHoldRepository = onHoldRepository;
        this.onHoldMapper = onHoldMapper;
        this.onHoldSearchRepository = onHoldSearchRepository;
    }

    @Override
    public Mono<OnHoldDTO> save(OnHoldDTO onHoldDTO) {
        log.debug("Request to save OnHold : {}", onHoldDTO);
        return onHoldRepository.save(onHoldMapper.toEntity(onHoldDTO)).flatMap(onHoldSearchRepository::save).map(onHoldMapper::toDto);
    }

    @Override
    public Mono<OnHoldDTO> update(OnHoldDTO onHoldDTO) {
        log.debug("Request to save OnHold : {}", onHoldDTO);
        return onHoldRepository
            .save(onHoldMapper.toEntity(onHoldDTO).setIsPersisted())
            .flatMap(onHoldSearchRepository::save)
            .map(onHoldMapper::toDto);
    }

    @Override
    public Mono<OnHoldDTO> partialUpdate(OnHoldDTO onHoldDTO) {
        log.debug("Request to partially update OnHold : {}", onHoldDTO);

        return onHoldRepository
            .findById(onHoldDTO.getId())
            .map(existingOnHold -> {
                onHoldMapper.partialUpdate(existingOnHold, onHoldDTO);

                return existingOnHold;
            })
            .flatMap(onHoldRepository::save)
            .flatMap(savedOnHold -> {
                onHoldSearchRepository.save(savedOnHold);

                return Mono.just(savedOnHold);
            })
            .map(onHoldMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OnHoldDTO> findAll() {
        log.debug("Request to get all OnHolds");
        return onHoldRepository.findAll().map(onHoldMapper::toDto);
    }

    public Mono<Long> countAll() {
        return onHoldRepository.count();
    }

    public Mono<Long> searchCount() {
        return onHoldSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OnHoldDTO> findOne(String id) {
        log.debug("Request to get OnHold : {}", id);
        return onHoldRepository.findById(id).map(onHoldMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete OnHold : {}", id);
        return onHoldRepository.deleteById(id).then(onHoldSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OnHoldDTO> search(String query) {
        log.debug("Request to search OnHolds for query {}", query);
        return onHoldSearchRepository.search(query).map(onHoldMapper::toDto);
    }
}
