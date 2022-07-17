package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.State;
import com.erestaurant.erestaurantapp.repository.StateRepository;
import com.erestaurant.erestaurantapp.repository.search.StateSearchRepository;
import com.erestaurant.erestaurantapp.service.StateService;
import com.erestaurant.erestaurantapp.service.dto.StateDTO;
import com.erestaurant.erestaurantapp.service.mapper.StateMapper;
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
 * Service Implementation for managing {@link State}.
 */
@Service
@Transactional
public class StateServiceImpl implements StateService {

    private final Logger log = LoggerFactory.getLogger(StateServiceImpl.class);

    private final StateRepository stateRepository;

    private final StateMapper stateMapper;

    private final StateSearchRepository stateSearchRepository;

    public StateServiceImpl(StateRepository stateRepository, StateMapper stateMapper, StateSearchRepository stateSearchRepository) {
        this.stateRepository = stateRepository;
        this.stateMapper = stateMapper;
        this.stateSearchRepository = stateSearchRepository;
    }

    @Override
    public Mono<StateDTO> save(StateDTO stateDTO) {
        log.debug("Request to save State : {}", stateDTO);
        return stateRepository.save(stateMapper.toEntity(stateDTO)).flatMap(stateSearchRepository::save).map(stateMapper::toDto);
    }

    @Override
    public Mono<StateDTO> update(StateDTO stateDTO) {
        log.debug("Request to save State : {}", stateDTO);
        return stateRepository
            .save(stateMapper.toEntity(stateDTO).setIsPersisted())
            .flatMap(stateSearchRepository::save)
            .map(stateMapper::toDto);
    }

    @Override
    public Mono<StateDTO> partialUpdate(StateDTO stateDTO) {
        log.debug("Request to partially update State : {}", stateDTO);

        return stateRepository
            .findById(stateDTO.getId())
            .map(existingState -> {
                stateMapper.partialUpdate(existingState, stateDTO);

                return existingState;
            })
            .flatMap(stateRepository::save)
            .flatMap(savedState -> {
                stateSearchRepository.save(savedState);

                return Mono.just(savedState);
            })
            .map(stateMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<StateDTO> findAll() {
        log.debug("Request to get all States");
        return stateRepository.findAll().map(stateMapper::toDto);
    }

    public Mono<Long> countAll() {
        return stateRepository.count();
    }

    public Mono<Long> searchCount() {
        return stateSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<StateDTO> findOne(String id) {
        log.debug("Request to get State : {}", id);
        return stateRepository.findById(id).map(stateMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete State : {}", id);
        return stateRepository.deleteById(id).then(stateSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<StateDTO> search(String query) {
        log.debug("Request to search States for query {}", query);
        return stateSearchRepository.search(query).map(stateMapper::toDto);
    }
}
