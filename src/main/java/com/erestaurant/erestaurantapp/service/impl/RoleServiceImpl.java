package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Role;
import com.erestaurant.erestaurantapp.repository.RoleRepository;
import com.erestaurant.erestaurantapp.repository.search.RoleSearchRepository;
import com.erestaurant.erestaurantapp.service.RoleService;
import com.erestaurant.erestaurantapp.service.dto.RoleDTO;
import com.erestaurant.erestaurantapp.service.mapper.RoleMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Role}.
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;

    private final RoleMapper roleMapper;

    private final RoleSearchRepository roleSearchRepository;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper, RoleSearchRepository roleSearchRepository) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.roleSearchRepository = roleSearchRepository;
    }

    @Override
    public Mono<RoleDTO> save(RoleDTO roleDTO) {
        log.debug("Request to save Role : {}", roleDTO);
        return roleRepository.save(roleMapper.toEntity(roleDTO)).flatMap(roleSearchRepository::save).map(roleMapper::toDto);
    }

    @Override
    public Mono<RoleDTO> update(RoleDTO roleDTO) {
        log.debug("Request to save Role : {}", roleDTO);
        return roleRepository
            .save(roleMapper.toEntity(roleDTO).setIsPersisted())
            .flatMap(roleSearchRepository::save)
            .map(roleMapper::toDto);
    }

    @Override
    public Mono<RoleDTO> partialUpdate(RoleDTO roleDTO) {
        log.debug("Request to partially update Role : {}", roleDTO);

        return roleRepository
            .findById(roleDTO.getId())
            .map(existingRole -> {
                roleMapper.partialUpdate(existingRole, roleDTO);

                return existingRole;
            })
            .flatMap(roleRepository::save)
            .flatMap(savedRole -> {
                roleSearchRepository.save(savedRole);

                return Mono.just(savedRole);
            })
            .map(roleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RoleDTO> findAll() {
        log.debug("Request to get all Roles");
        return roleRepository.findAllWithEagerRelationships().map(roleMapper::toDto);
    }

    public Flux<RoleDTO> findAllWithEagerRelationships(Pageable pageable) {
        return roleRepository.findAllWithEagerRelationships(pageable).map(roleMapper::toDto);
    }

    public Mono<Long> countAll() {
        return roleRepository.count();
    }

    public Mono<Long> searchCount() {
        return roleSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RoleDTO> findOne(String id) {
        log.debug("Request to get Role : {}", id);
        return roleRepository.findOneWithEagerRelationships(id).map(roleMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Role : {}", id);
        return roleRepository.deleteById(id).then(roleSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RoleDTO> search(String query) {
        log.debug("Request to search Roles for query {}", query);
        return roleSearchRepository.search(query).map(roleMapper::toDto);
    }
}
