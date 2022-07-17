package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.NewOrder;
import com.erestaurant.erestaurantapp.repository.NewOrderRepository;
import com.erestaurant.erestaurantapp.repository.search.NewOrderSearchRepository;
import com.erestaurant.erestaurantapp.service.NewOrderService;
import com.erestaurant.erestaurantapp.service.dto.NewOrderDTO;
import com.erestaurant.erestaurantapp.service.mapper.NewOrderMapper;
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
 * Service Implementation for managing {@link NewOrder}.
 */
@Service
@Transactional
public class NewOrderServiceImpl implements NewOrderService {

    private final Logger log = LoggerFactory.getLogger(NewOrderServiceImpl.class);

    private final NewOrderRepository newOrderRepository;

    private final NewOrderMapper newOrderMapper;

    private final NewOrderSearchRepository newOrderSearchRepository;

    public NewOrderServiceImpl(
        NewOrderRepository newOrderRepository,
        NewOrderMapper newOrderMapper,
        NewOrderSearchRepository newOrderSearchRepository
    ) {
        this.newOrderRepository = newOrderRepository;
        this.newOrderMapper = newOrderMapper;
        this.newOrderSearchRepository = newOrderSearchRepository;
    }

    @Override
    public Mono<NewOrderDTO> save(NewOrderDTO newOrderDTO) {
        log.debug("Request to save NewOrder : {}", newOrderDTO);
        return newOrderRepository
            .save(newOrderMapper.toEntity(newOrderDTO))
            .flatMap(newOrderSearchRepository::save)
            .map(newOrderMapper::toDto);
    }

    @Override
    public Mono<NewOrderDTO> update(NewOrderDTO newOrderDTO) {
        log.debug("Request to save NewOrder : {}", newOrderDTO);
        return newOrderRepository
            .save(newOrderMapper.toEntity(newOrderDTO).setIsPersisted())
            .flatMap(newOrderSearchRepository::save)
            .map(newOrderMapper::toDto);
    }

    @Override
    public Mono<NewOrderDTO> partialUpdate(NewOrderDTO newOrderDTO) {
        log.debug("Request to partially update NewOrder : {}", newOrderDTO);

        return newOrderRepository
            .findById(newOrderDTO.getId())
            .map(existingNewOrder -> {
                newOrderMapper.partialUpdate(existingNewOrder, newOrderDTO);

                return existingNewOrder;
            })
            .flatMap(newOrderRepository::save)
            .flatMap(savedNewOrder -> {
                newOrderSearchRepository.save(savedNewOrder);

                return Mono.just(savedNewOrder);
            })
            .map(newOrderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<NewOrderDTO> findAll() {
        log.debug("Request to get all NewOrders");
        return newOrderRepository.findAll().map(newOrderMapper::toDto);
    }

    public Mono<Long> countAll() {
        return newOrderRepository.count();
    }

    public Mono<Long> searchCount() {
        return newOrderSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<NewOrderDTO> findOne(String id) {
        log.debug("Request to get NewOrder : {}", id);
        return newOrderRepository.findById(id).map(newOrderMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete NewOrder : {}", id);
        return newOrderRepository.deleteById(id).then(newOrderSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<NewOrderDTO> search(String query) {
        log.debug("Request to search NewOrders for query {}", query);
        return newOrderSearchRepository.search(query).map(newOrderMapper::toDto);
    }
}
