package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Close;
import com.erestaurant.erestaurantapp.repository.CloseRepository;
import com.erestaurant.erestaurantapp.repository.search.CloseSearchRepository;
import com.erestaurant.erestaurantapp.service.CloseService;
import com.erestaurant.erestaurantapp.service.dto.CloseDTO;
import com.erestaurant.erestaurantapp.service.mapper.CloseMapper;
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
 * Service Implementation for managing {@link Close}.
 */
@Service
@Transactional
public class CloseServiceImpl implements CloseService {

    private final Logger log = LoggerFactory.getLogger(CloseServiceImpl.class);

    private final CloseRepository closeRepository;

    private final CloseMapper closeMapper;

    private final CloseSearchRepository closeSearchRepository;

    public CloseServiceImpl(CloseRepository closeRepository, CloseMapper closeMapper, CloseSearchRepository closeSearchRepository) {
        this.closeRepository = closeRepository;
        this.closeMapper = closeMapper;
        this.closeSearchRepository = closeSearchRepository;
    }

    @Override
    public Mono<CloseDTO> save(CloseDTO closeDTO) {
        log.debug("Request to save Close : {}", closeDTO);
        return closeRepository.save(closeMapper.toEntity(closeDTO)).flatMap(closeSearchRepository::save).map(closeMapper::toDto);
    }

    @Override
    public Mono<CloseDTO> update(CloseDTO closeDTO) {
        log.debug("Request to save Close : {}", closeDTO);
        return closeRepository
            .save(closeMapper.toEntity(closeDTO).setIsPersisted())
            .flatMap(closeSearchRepository::save)
            .map(closeMapper::toDto);
    }

    @Override
    public Mono<CloseDTO> partialUpdate(CloseDTO closeDTO) {
        log.debug("Request to partially update Close : {}", closeDTO);

        return closeRepository
            .findById(closeDTO.getId())
            .map(existingClose -> {
                closeMapper.partialUpdate(existingClose, closeDTO);

                return existingClose;
            })
            .flatMap(closeRepository::save)
            .flatMap(savedClose -> {
                closeSearchRepository.save(savedClose);

                return Mono.just(savedClose);
            })
            .map(closeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CloseDTO> findAll() {
        log.debug("Request to get all Closes");
        return closeRepository.findAll().map(closeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return closeRepository.count();
    }

    public Mono<Long> searchCount() {
        return closeSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CloseDTO> findOne(String id) {
        log.debug("Request to get Close : {}", id);
        return closeRepository.findById(id).map(closeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Close : {}", id);
        return closeRepository.deleteById(id).then(closeSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CloseDTO> search(String query) {
        log.debug("Request to search Closes for query {}", query);
        return closeSearchRepository.search(query).map(closeMapper::toDto);
    }
}
