package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Refunded;
import com.erestaurant.erestaurantapp.repository.RefundedRepository;
import com.erestaurant.erestaurantapp.repository.search.RefundedSearchRepository;
import com.erestaurant.erestaurantapp.service.RefundedService;
import com.erestaurant.erestaurantapp.service.dto.RefundedDTO;
import com.erestaurant.erestaurantapp.service.mapper.RefundedMapper;
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
 * Service Implementation for managing {@link Refunded}.
 */
@Service
@Transactional
public class RefundedServiceImpl implements RefundedService {

    private final Logger log = LoggerFactory.getLogger(RefundedServiceImpl.class);

    private final RefundedRepository refundedRepository;

    private final RefundedMapper refundedMapper;

    private final RefundedSearchRepository refundedSearchRepository;

    public RefundedServiceImpl(
        RefundedRepository refundedRepository,
        RefundedMapper refundedMapper,
        RefundedSearchRepository refundedSearchRepository
    ) {
        this.refundedRepository = refundedRepository;
        this.refundedMapper = refundedMapper;
        this.refundedSearchRepository = refundedSearchRepository;
    }

    @Override
    public Mono<RefundedDTO> save(RefundedDTO refundedDTO) {
        log.debug("Request to save Refunded : {}", refundedDTO);
        return refundedRepository
            .save(refundedMapper.toEntity(refundedDTO))
            .flatMap(refundedSearchRepository::save)
            .map(refundedMapper::toDto);
    }

    @Override
    public Mono<RefundedDTO> update(RefundedDTO refundedDTO) {
        log.debug("Request to save Refunded : {}", refundedDTO);
        return refundedRepository
            .save(refundedMapper.toEntity(refundedDTO).setIsPersisted())
            .flatMap(refundedSearchRepository::save)
            .map(refundedMapper::toDto);
    }

    @Override
    public Mono<RefundedDTO> partialUpdate(RefundedDTO refundedDTO) {
        log.debug("Request to partially update Refunded : {}", refundedDTO);

        return refundedRepository
            .findById(refundedDTO.getId())
            .map(existingRefunded -> {
                refundedMapper.partialUpdate(existingRefunded, refundedDTO);

                return existingRefunded;
            })
            .flatMap(refundedRepository::save)
            .flatMap(savedRefunded -> {
                refundedSearchRepository.save(savedRefunded);

                return Mono.just(savedRefunded);
            })
            .map(refundedMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RefundedDTO> findAll() {
        log.debug("Request to get all Refundeds");
        return refundedRepository.findAll().map(refundedMapper::toDto);
    }

    public Mono<Long> countAll() {
        return refundedRepository.count();
    }

    public Mono<Long> searchCount() {
        return refundedSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RefundedDTO> findOne(String id) {
        log.debug("Request to get Refunded : {}", id);
        return refundedRepository.findById(id).map(refundedMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Refunded : {}", id);
        return refundedRepository.deleteById(id).then(refundedSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RefundedDTO> search(String query) {
        log.debug("Request to search Refundeds for query {}", query);
        return refundedSearchRepository.search(query).map(refundedMapper::toDto);
    }
}
