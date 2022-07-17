package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Complete;
import com.erestaurant.erestaurantapp.repository.CompleteRepository;
import com.erestaurant.erestaurantapp.repository.search.CompleteSearchRepository;
import com.erestaurant.erestaurantapp.service.CompleteService;
import com.erestaurant.erestaurantapp.service.dto.CompleteDTO;
import com.erestaurant.erestaurantapp.service.mapper.CompleteMapper;
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
 * Service Implementation for managing {@link Complete}.
 */
@Service
@Transactional
public class CompleteServiceImpl implements CompleteService {

    private final Logger log = LoggerFactory.getLogger(CompleteServiceImpl.class);

    private final CompleteRepository completeRepository;

    private final CompleteMapper completeMapper;

    private final CompleteSearchRepository completeSearchRepository;

    public CompleteServiceImpl(
        CompleteRepository completeRepository,
        CompleteMapper completeMapper,
        CompleteSearchRepository completeSearchRepository
    ) {
        this.completeRepository = completeRepository;
        this.completeMapper = completeMapper;
        this.completeSearchRepository = completeSearchRepository;
    }

    @Override
    public Mono<CompleteDTO> save(CompleteDTO completeDTO) {
        log.debug("Request to save Complete : {}", completeDTO);
        return completeRepository
            .save(completeMapper.toEntity(completeDTO))
            .flatMap(completeSearchRepository::save)
            .map(completeMapper::toDto);
    }

    @Override
    public Mono<CompleteDTO> update(CompleteDTO completeDTO) {
        log.debug("Request to save Complete : {}", completeDTO);
        return completeRepository
            .save(completeMapper.toEntity(completeDTO).setIsPersisted())
            .flatMap(completeSearchRepository::save)
            .map(completeMapper::toDto);
    }

    @Override
    public Mono<CompleteDTO> partialUpdate(CompleteDTO completeDTO) {
        log.debug("Request to partially update Complete : {}", completeDTO);

        return completeRepository
            .findById(completeDTO.getId())
            .map(existingComplete -> {
                completeMapper.partialUpdate(existingComplete, completeDTO);

                return existingComplete;
            })
            .flatMap(completeRepository::save)
            .flatMap(savedComplete -> {
                completeSearchRepository.save(savedComplete);

                return Mono.just(savedComplete);
            })
            .map(completeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CompleteDTO> findAll() {
        log.debug("Request to get all Completes");
        return completeRepository.findAll().map(completeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return completeRepository.count();
    }

    public Mono<Long> searchCount() {
        return completeSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CompleteDTO> findOne(String id) {
        log.debug("Request to get Complete : {}", id);
        return completeRepository.findById(id).map(completeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Complete : {}", id);
        return completeRepository.deleteById(id).then(completeSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CompleteDTO> search(String query) {
        log.debug("Request to search Completes for query {}", query);
        return completeSearchRepository.search(query).map(completeMapper::toDto);
    }
}
