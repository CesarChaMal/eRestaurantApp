package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.PermissionComposite;
import com.erestaurant.erestaurantapp.repository.PermissionCompositeRepository;
import com.erestaurant.erestaurantapp.repository.search.PermissionCompositeSearchRepository;
import com.erestaurant.erestaurantapp.service.PermissionCompositeService;
import com.erestaurant.erestaurantapp.service.dto.PermissionCompositeDTO;
import com.erestaurant.erestaurantapp.service.mapper.PermissionCompositeMapper;
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
 * Service Implementation for managing {@link PermissionComposite}.
 */
@Service
@Transactional
public class PermissionCompositeServiceImpl implements PermissionCompositeService {

    private final Logger log = LoggerFactory.getLogger(PermissionCompositeServiceImpl.class);

    private final PermissionCompositeRepository permissionCompositeRepository;

    private final PermissionCompositeMapper permissionCompositeMapper;

    private final PermissionCompositeSearchRepository permissionCompositeSearchRepository;

    public PermissionCompositeServiceImpl(
        PermissionCompositeRepository permissionCompositeRepository,
        PermissionCompositeMapper permissionCompositeMapper,
        PermissionCompositeSearchRepository permissionCompositeSearchRepository
    ) {
        this.permissionCompositeRepository = permissionCompositeRepository;
        this.permissionCompositeMapper = permissionCompositeMapper;
        this.permissionCompositeSearchRepository = permissionCompositeSearchRepository;
    }

    @Override
    public Mono<PermissionCompositeDTO> save(PermissionCompositeDTO permissionCompositeDTO) {
        log.debug("Request to save PermissionComposite : {}", permissionCompositeDTO);
        return permissionCompositeRepository
            .save(permissionCompositeMapper.toEntity(permissionCompositeDTO))
            .flatMap(permissionCompositeSearchRepository::save)
            .map(permissionCompositeMapper::toDto);
    }

    @Override
    public Mono<PermissionCompositeDTO> update(PermissionCompositeDTO permissionCompositeDTO) {
        log.debug("Request to save PermissionComposite : {}", permissionCompositeDTO);
        return permissionCompositeRepository
            .save(permissionCompositeMapper.toEntity(permissionCompositeDTO).setIsPersisted())
            .flatMap(permissionCompositeSearchRepository::save)
            .map(permissionCompositeMapper::toDto);
    }

    @Override
    public Mono<PermissionCompositeDTO> partialUpdate(PermissionCompositeDTO permissionCompositeDTO) {
        log.debug("Request to partially update PermissionComposite : {}", permissionCompositeDTO);

        return permissionCompositeRepository
            .findById(permissionCompositeDTO.getId())
            .map(existingPermissionComposite -> {
                permissionCompositeMapper.partialUpdate(existingPermissionComposite, permissionCompositeDTO);

                return existingPermissionComposite;
            })
            .flatMap(permissionCompositeRepository::save)
            .flatMap(savedPermissionComposite -> {
                permissionCompositeSearchRepository.save(savedPermissionComposite);

                return Mono.just(savedPermissionComposite);
            })
            .map(permissionCompositeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PermissionCompositeDTO> findAll() {
        log.debug("Request to get all PermissionComposites");
        return permissionCompositeRepository.findAll().map(permissionCompositeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return permissionCompositeRepository.count();
    }

    public Mono<Long> searchCount() {
        return permissionCompositeSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PermissionCompositeDTO> findOne(String id) {
        log.debug("Request to get PermissionComposite : {}", id);
        return permissionCompositeRepository.findById(id).map(permissionCompositeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete PermissionComposite : {}", id);
        return permissionCompositeRepository.deleteById(id).then(permissionCompositeSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PermissionCompositeDTO> search(String query) {
        log.debug("Request to search PermissionComposites for query {}", query);
        return permissionCompositeSearchRepository.search(query).map(permissionCompositeMapper::toDto);
    }
}
