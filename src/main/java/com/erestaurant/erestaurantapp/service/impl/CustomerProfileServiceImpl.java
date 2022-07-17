package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.CustomerProfile;
import com.erestaurant.erestaurantapp.repository.CustomerProfileRepository;
import com.erestaurant.erestaurantapp.repository.search.CustomerProfileSearchRepository;
import com.erestaurant.erestaurantapp.service.CustomerProfileService;
import com.erestaurant.erestaurantapp.service.dto.CustomerProfileDTO;
import com.erestaurant.erestaurantapp.service.mapper.CustomerProfileMapper;
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
 * Service Implementation for managing {@link CustomerProfile}.
 */
@Service
@Transactional
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final Logger log = LoggerFactory.getLogger(CustomerProfileServiceImpl.class);

    private final CustomerProfileRepository customerProfileRepository;

    private final CustomerProfileMapper customerProfileMapper;

    private final CustomerProfileSearchRepository customerProfileSearchRepository;

    public CustomerProfileServiceImpl(
        CustomerProfileRepository customerProfileRepository,
        CustomerProfileMapper customerProfileMapper,
        CustomerProfileSearchRepository customerProfileSearchRepository
    ) {
        this.customerProfileRepository = customerProfileRepository;
        this.customerProfileMapper = customerProfileMapper;
        this.customerProfileSearchRepository = customerProfileSearchRepository;
    }

    @Override
    public Mono<CustomerProfileDTO> save(CustomerProfileDTO customerProfileDTO) {
        log.debug("Request to save CustomerProfile : {}", customerProfileDTO);
        return customerProfileRepository
            .save(customerProfileMapper.toEntity(customerProfileDTO))
            .flatMap(customerProfileSearchRepository::save)
            .map(customerProfileMapper::toDto);
    }

    @Override
    public Mono<CustomerProfileDTO> update(CustomerProfileDTO customerProfileDTO) {
        log.debug("Request to save CustomerProfile : {}", customerProfileDTO);
        return customerProfileRepository
            .save(customerProfileMapper.toEntity(customerProfileDTO).setIsPersisted())
            .flatMap(customerProfileSearchRepository::save)
            .map(customerProfileMapper::toDto);
    }

    @Override
    public Mono<CustomerProfileDTO> partialUpdate(CustomerProfileDTO customerProfileDTO) {
        log.debug("Request to partially update CustomerProfile : {}", customerProfileDTO);

        return customerProfileRepository
            .findById(customerProfileDTO.getId())
            .map(existingCustomerProfile -> {
                customerProfileMapper.partialUpdate(existingCustomerProfile, customerProfileDTO);

                return existingCustomerProfile;
            })
            .flatMap(customerProfileRepository::save)
            .flatMap(savedCustomerProfile -> {
                customerProfileSearchRepository.save(savedCustomerProfile);

                return Mono.just(savedCustomerProfile);
            })
            .map(customerProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CustomerProfileDTO> findAll() {
        log.debug("Request to get all CustomerProfiles");
        return customerProfileRepository.findAll().map(customerProfileMapper::toDto);
    }

    public Mono<Long> countAll() {
        return customerProfileRepository.count();
    }

    public Mono<Long> searchCount() {
        return customerProfileSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CustomerProfileDTO> findOne(String id) {
        log.debug("Request to get CustomerProfile : {}", id);
        return customerProfileRepository.findById(id).map(customerProfileMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete CustomerProfile : {}", id);
        return customerProfileRepository.deleteById(id).then(customerProfileSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CustomerProfileDTO> search(String query) {
        log.debug("Request to search CustomerProfiles for query {}", query);
        return customerProfileSearchRepository.search(query).map(customerProfileMapper::toDto);
    }
}
