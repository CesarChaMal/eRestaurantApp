package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Permission;
import com.erestaurant.erestaurantapp.repository.PermissionRepository;
import com.erestaurant.erestaurantapp.repository.search.PermissionSearchRepository;
import com.erestaurant.erestaurantapp.service.PermissionService;
import com.erestaurant.erestaurantapp.service.dto.PermissionDTO;
import com.erestaurant.erestaurantapp.service.mapper.PermissionMapper;
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
 * Service Implementation for managing {@link Permission}.
 */
@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final Logger log = LoggerFactory.getLogger(PermissionServiceImpl.class);

    private final PermissionRepository permissionRepository;

    private final PermissionMapper permissionMapper;

    private final PermissionSearchRepository permissionSearchRepository;

    public PermissionServiceImpl(
        PermissionRepository permissionRepository,
        PermissionMapper permissionMapper,
        PermissionSearchRepository permissionSearchRepository
    ) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
        this.permissionSearchRepository = permissionSearchRepository;
    }

    @Override
    public Mono<PermissionDTO> save(PermissionDTO permissionDTO) {
        log.debug("Request to save Permission : {}", permissionDTO);
        return permissionRepository
            .save(permissionMapper.toEntity(permissionDTO))
            .flatMap(permissionSearchRepository::save)
            .map(permissionMapper::toDto);
    }

    @Override
    public Mono<PermissionDTO> update(PermissionDTO permissionDTO) {
        log.debug("Request to save Permission : {}", permissionDTO);
        return permissionRepository
            .save(permissionMapper.toEntity(permissionDTO).setIsPersisted())
            .flatMap(permissionSearchRepository::save)
            .map(permissionMapper::toDto);
    }

    @Override
    public Mono<PermissionDTO> partialUpdate(PermissionDTO permissionDTO) {
        log.debug("Request to partially update Permission : {}", permissionDTO);

        return permissionRepository
            .findById(permissionDTO.getId())
            .map(existingPermission -> {
                permissionMapper.partialUpdate(existingPermission, permissionDTO);

                return existingPermission;
            })
            .flatMap(permissionRepository::save)
            .flatMap(savedPermission -> {
                permissionSearchRepository.save(savedPermission);

                return Mono.just(savedPermission);
            })
            .map(permissionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PermissionDTO> findAll() {
        log.debug("Request to get all Permissions");
        return permissionRepository.findAll().map(permissionMapper::toDto);
    }

    public Mono<Long> countAll() {
        return permissionRepository.count();
    }

    public Mono<Long> searchCount() {
        return permissionSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PermissionDTO> findOne(String id) {
        log.debug("Request to get Permission : {}", id);
        return permissionRepository.findById(id).map(permissionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Permission : {}", id);
        return permissionRepository.deleteById(id).then(permissionSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PermissionDTO> search(String query) {
        log.debug("Request to search Permissions for query {}", query);
        return permissionSearchRepository.search(query).map(permissionMapper::toDto);
    }
}
