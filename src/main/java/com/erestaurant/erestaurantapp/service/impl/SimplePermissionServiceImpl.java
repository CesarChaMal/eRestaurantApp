package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.SimplePermission;
import com.erestaurant.erestaurantapp.repository.SimplePermissionRepository;
import com.erestaurant.erestaurantapp.repository.search.SimplePermissionSearchRepository;
import com.erestaurant.erestaurantapp.service.SimplePermissionService;
import com.erestaurant.erestaurantapp.service.dto.SimplePermissionDTO;
import com.erestaurant.erestaurantapp.service.mapper.SimplePermissionMapper;
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
 * Service Implementation for managing {@link SimplePermission}.
 */
@Service
@Transactional
public class SimplePermissionServiceImpl implements SimplePermissionService {

    private final Logger log = LoggerFactory.getLogger(SimplePermissionServiceImpl.class);

    private final SimplePermissionRepository simplePermissionRepository;

    private final SimplePermissionMapper simplePermissionMapper;

    private final SimplePermissionSearchRepository simplePermissionSearchRepository;

    public SimplePermissionServiceImpl(
        SimplePermissionRepository simplePermissionRepository,
        SimplePermissionMapper simplePermissionMapper,
        SimplePermissionSearchRepository simplePermissionSearchRepository
    ) {
        this.simplePermissionRepository = simplePermissionRepository;
        this.simplePermissionMapper = simplePermissionMapper;
        this.simplePermissionSearchRepository = simplePermissionSearchRepository;
    }

    @Override
    public Mono<SimplePermissionDTO> save(SimplePermissionDTO simplePermissionDTO) {
        log.debug("Request to save SimplePermission : {}", simplePermissionDTO);
        return simplePermissionRepository
            .save(simplePermissionMapper.toEntity(simplePermissionDTO))
            .flatMap(simplePermissionSearchRepository::save)
            .map(simplePermissionMapper::toDto);
    }

    @Override
    public Mono<SimplePermissionDTO> update(SimplePermissionDTO simplePermissionDTO) {
        log.debug("Request to save SimplePermission : {}", simplePermissionDTO);
        return simplePermissionRepository
            .save(simplePermissionMapper.toEntity(simplePermissionDTO).setIsPersisted())
            .flatMap(simplePermissionSearchRepository::save)
            .map(simplePermissionMapper::toDto);
    }

    @Override
    public Mono<SimplePermissionDTO> partialUpdate(SimplePermissionDTO simplePermissionDTO) {
        log.debug("Request to partially update SimplePermission : {}", simplePermissionDTO);

        return simplePermissionRepository
            .findById(simplePermissionDTO.getId())
            .map(existingSimplePermission -> {
                simplePermissionMapper.partialUpdate(existingSimplePermission, simplePermissionDTO);

                return existingSimplePermission;
            })
            .flatMap(simplePermissionRepository::save)
            .flatMap(savedSimplePermission -> {
                simplePermissionSearchRepository.save(savedSimplePermission);

                return Mono.just(savedSimplePermission);
            })
            .map(simplePermissionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SimplePermissionDTO> findAll() {
        log.debug("Request to get all SimplePermissions");
        return simplePermissionRepository.findAll().map(simplePermissionMapper::toDto);
    }

    public Mono<Long> countAll() {
        return simplePermissionRepository.count();
    }

    public Mono<Long> searchCount() {
        return simplePermissionSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SimplePermissionDTO> findOne(String id) {
        log.debug("Request to get SimplePermission : {}", id);
        return simplePermissionRepository.findById(id).map(simplePermissionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete SimplePermission : {}", id);
        return simplePermissionRepository.deleteById(id).then(simplePermissionSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SimplePermissionDTO> search(String query) {
        log.debug("Request to search SimplePermissions for query {}", query);
        return simplePermissionSearchRepository.search(query).map(simplePermissionMapper::toDto);
    }
}
