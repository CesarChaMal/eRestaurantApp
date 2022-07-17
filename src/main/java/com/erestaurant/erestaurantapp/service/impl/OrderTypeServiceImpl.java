package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.OrderType;
import com.erestaurant.erestaurantapp.repository.OrderTypeRepository;
import com.erestaurant.erestaurantapp.repository.search.OrderTypeSearchRepository;
import com.erestaurant.erestaurantapp.service.OrderTypeService;
import com.erestaurant.erestaurantapp.service.dto.OrderTypeDTO;
import com.erestaurant.erestaurantapp.service.mapper.OrderTypeMapper;
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
 * Service Implementation for managing {@link OrderType}.
 */
@Service
@Transactional
public class OrderTypeServiceImpl implements OrderTypeService {

    private final Logger log = LoggerFactory.getLogger(OrderTypeServiceImpl.class);

    private final OrderTypeRepository orderTypeRepository;

    private final OrderTypeMapper orderTypeMapper;

    private final OrderTypeSearchRepository orderTypeSearchRepository;

    public OrderTypeServiceImpl(
        OrderTypeRepository orderTypeRepository,
        OrderTypeMapper orderTypeMapper,
        OrderTypeSearchRepository orderTypeSearchRepository
    ) {
        this.orderTypeRepository = orderTypeRepository;
        this.orderTypeMapper = orderTypeMapper;
        this.orderTypeSearchRepository = orderTypeSearchRepository;
    }

    @Override
    public Mono<OrderTypeDTO> save(OrderTypeDTO orderTypeDTO) {
        log.debug("Request to save OrderType : {}", orderTypeDTO);
        return orderTypeRepository
            .save(orderTypeMapper.toEntity(orderTypeDTO))
            .flatMap(orderTypeSearchRepository::save)
            .map(orderTypeMapper::toDto);
    }

    @Override
    public Mono<OrderTypeDTO> update(OrderTypeDTO orderTypeDTO) {
        log.debug("Request to save OrderType : {}", orderTypeDTO);
        return orderTypeRepository
            .save(orderTypeMapper.toEntity(orderTypeDTO).setIsPersisted())
            .flatMap(orderTypeSearchRepository::save)
            .map(orderTypeMapper::toDto);
    }

    @Override
    public Mono<OrderTypeDTO> partialUpdate(OrderTypeDTO orderTypeDTO) {
        log.debug("Request to partially update OrderType : {}", orderTypeDTO);

        return orderTypeRepository
            .findById(orderTypeDTO.getId())
            .map(existingOrderType -> {
                orderTypeMapper.partialUpdate(existingOrderType, orderTypeDTO);

                return existingOrderType;
            })
            .flatMap(orderTypeRepository::save)
            .flatMap(savedOrderType -> {
                orderTypeSearchRepository.save(savedOrderType);

                return Mono.just(savedOrderType);
            })
            .map(orderTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OrderTypeDTO> findAll() {
        log.debug("Request to get all OrderTypes");
        return orderTypeRepository.findAll().map(orderTypeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return orderTypeRepository.count();
    }

    public Mono<Long> searchCount() {
        return orderTypeSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OrderTypeDTO> findOne(String id) {
        log.debug("Request to get OrderType : {}", id);
        return orderTypeRepository.findById(id).map(orderTypeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete OrderType : {}", id);
        return orderTypeRepository.deleteById(id).then(orderTypeSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<OrderTypeDTO> search(String query) {
        log.debug("Request to search OrderTypes for query {}", query);
        return orderTypeSearchRepository.search(query).map(orderTypeMapper::toDto);
    }
}
