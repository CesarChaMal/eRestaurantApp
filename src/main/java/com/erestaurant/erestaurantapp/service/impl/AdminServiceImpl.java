package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Admin;
import com.erestaurant.erestaurantapp.repository.AdminRepository;
import com.erestaurant.erestaurantapp.repository.search.AdminSearchRepository;
import com.erestaurant.erestaurantapp.service.AdminService;
import com.erestaurant.erestaurantapp.service.dto.AdminDTO;
import com.erestaurant.erestaurantapp.service.mapper.AdminMapper;
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
 * Service Implementation for managing {@link Admin}.
 */
@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final AdminRepository adminRepository;

    private final AdminMapper adminMapper;

    private final AdminSearchRepository adminSearchRepository;

    public AdminServiceImpl(AdminRepository adminRepository, AdminMapper adminMapper, AdminSearchRepository adminSearchRepository) {
        this.adminRepository = adminRepository;
        this.adminMapper = adminMapper;
        this.adminSearchRepository = adminSearchRepository;
    }

    @Override
    public Mono<AdminDTO> save(AdminDTO adminDTO) {
        log.debug("Request to save Admin : {}", adminDTO);
        return adminRepository.save(adminMapper.toEntity(adminDTO)).flatMap(adminSearchRepository::save).map(adminMapper::toDto);
    }

    @Override
    public Mono<AdminDTO> update(AdminDTO adminDTO) {
        log.debug("Request to save Admin : {}", adminDTO);
        return adminRepository
            .save(adminMapper.toEntity(adminDTO).setIsPersisted())
            .flatMap(adminSearchRepository::save)
            .map(adminMapper::toDto);
    }

    @Override
    public Mono<AdminDTO> partialUpdate(AdminDTO adminDTO) {
        log.debug("Request to partially update Admin : {}", adminDTO);

        return adminRepository
            .findById(adminDTO.getId())
            .map(existingAdmin -> {
                adminMapper.partialUpdate(existingAdmin, adminDTO);

                return existingAdmin;
            })
            .flatMap(adminRepository::save)
            .flatMap(savedAdmin -> {
                adminSearchRepository.save(savedAdmin);

                return Mono.just(savedAdmin);
            })
            .map(adminMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AdminDTO> findAll() {
        log.debug("Request to get all Admins");
        return adminRepository.findAll().map(adminMapper::toDto);
    }

    public Mono<Long> countAll() {
        return adminRepository.count();
    }

    public Mono<Long> searchCount() {
        return adminSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AdminDTO> findOne(String id) {
        log.debug("Request to get Admin : {}", id);
        return adminRepository.findById(id).map(adminMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Admin : {}", id);
        return adminRepository.deleteById(id).then(adminSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AdminDTO> search(String query) {
        log.debug("Request to search Admins for query {}", query);
        return adminSearchRepository.search(query).map(adminMapper::toDto);
    }
}
