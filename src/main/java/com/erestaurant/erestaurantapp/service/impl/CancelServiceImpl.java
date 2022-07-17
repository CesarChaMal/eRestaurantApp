package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Cancel;
import com.erestaurant.erestaurantapp.repository.CancelRepository;
import com.erestaurant.erestaurantapp.repository.search.CancelSearchRepository;
import com.erestaurant.erestaurantapp.service.CancelService;
import com.erestaurant.erestaurantapp.service.dto.CancelDTO;
import com.erestaurant.erestaurantapp.service.mapper.CancelMapper;
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
 * Service Implementation for managing {@link Cancel}.
 */
@Service
@Transactional
public class CancelServiceImpl implements CancelService {

    private final Logger log = LoggerFactory.getLogger(CancelServiceImpl.class);

    private final CancelRepository cancelRepository;

    private final CancelMapper cancelMapper;

    private final CancelSearchRepository cancelSearchRepository;

    public CancelServiceImpl(CancelRepository cancelRepository, CancelMapper cancelMapper, CancelSearchRepository cancelSearchRepository) {
        this.cancelRepository = cancelRepository;
        this.cancelMapper = cancelMapper;
        this.cancelSearchRepository = cancelSearchRepository;
    }

    @Override
    public Mono<CancelDTO> save(CancelDTO cancelDTO) {
        log.debug("Request to save Cancel : {}", cancelDTO);
        return cancelRepository.save(cancelMapper.toEntity(cancelDTO)).flatMap(cancelSearchRepository::save).map(cancelMapper::toDto);
    }

    @Override
    public Mono<CancelDTO> update(CancelDTO cancelDTO) {
        log.debug("Request to save Cancel : {}", cancelDTO);
        return cancelRepository
            .save(cancelMapper.toEntity(cancelDTO).setIsPersisted())
            .flatMap(cancelSearchRepository::save)
            .map(cancelMapper::toDto);
    }

    @Override
    public Mono<CancelDTO> partialUpdate(CancelDTO cancelDTO) {
        log.debug("Request to partially update Cancel : {}", cancelDTO);

        return cancelRepository
            .findById(cancelDTO.getId())
            .map(existingCancel -> {
                cancelMapper.partialUpdate(existingCancel, cancelDTO);

                return existingCancel;
            })
            .flatMap(cancelRepository::save)
            .flatMap(savedCancel -> {
                cancelSearchRepository.save(savedCancel);

                return Mono.just(savedCancel);
            })
            .map(cancelMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CancelDTO> findAll() {
        log.debug("Request to get all Cancels");
        return cancelRepository.findAll().map(cancelMapper::toDto);
    }

    public Mono<Long> countAll() {
        return cancelRepository.count();
    }

    public Mono<Long> searchCount() {
        return cancelSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CancelDTO> findOne(String id) {
        log.debug("Request to get Cancel : {}", id);
        return cancelRepository.findById(id).map(cancelMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Cancel : {}", id);
        return cancelRepository.deleteById(id).then(cancelSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CancelDTO> search(String query) {
        log.debug("Request to search Cancels for query {}", query);
        return cancelSearchRepository.search(query).map(cancelMapper::toDto);
    }
}
