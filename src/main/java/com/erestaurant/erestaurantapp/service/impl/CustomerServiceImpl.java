package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Customer;
import com.erestaurant.erestaurantapp.repository.CustomerRepository;
import com.erestaurant.erestaurantapp.repository.search.CustomerSearchRepository;
import com.erestaurant.erestaurantapp.service.CustomerService;
import com.erestaurant.erestaurantapp.service.dto.CustomerDTO;
import com.erestaurant.erestaurantapp.service.mapper.CustomerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Customer}.
 */
@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    private final CustomerSearchRepository customerSearchRepository;

    public CustomerServiceImpl(
        CustomerRepository customerRepository,
        CustomerMapper customerMapper,
        CustomerSearchRepository customerSearchRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.customerSearchRepository = customerSearchRepository;
    }

    @Override
    public Mono<CustomerDTO> save(CustomerDTO customerDTO) {
        log.debug("Request to save Customer : {}", customerDTO);
        return customerRepository
            .save(customerMapper.toEntity(customerDTO))
            .flatMap(customerSearchRepository::save)
            .map(customerMapper::toDto);
    }

    @Override
    public Mono<CustomerDTO> update(CustomerDTO customerDTO) {
        log.debug("Request to save Customer : {}", customerDTO);
        return customerRepository
            .save(customerMapper.toEntity(customerDTO).setIsPersisted())
            .flatMap(customerSearchRepository::save)
            .map(customerMapper::toDto);
    }

    @Override
    public Mono<CustomerDTO> partialUpdate(CustomerDTO customerDTO) {
        log.debug("Request to partially update Customer : {}", customerDTO);

        return customerRepository
            .findById(customerDTO.getId())
            .map(existingCustomer -> {
                customerMapper.partialUpdate(existingCustomer, customerDTO);

                return existingCustomer;
            })
            .flatMap(customerRepository::save)
            .flatMap(savedCustomer -> {
                customerSearchRepository.save(savedCustomer);

                return Mono.just(savedCustomer);
            })
            .map(customerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CustomerDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Customers");
        return customerRepository.findAllBy(pageable).map(customerMapper::toDto);
    }

    public Mono<Long> countAll() {
        return customerRepository.count();
    }

    public Mono<Long> searchCount() {
        return customerSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CustomerDTO> findOne(String id) {
        log.debug("Request to get Customer : {}", id);
        return customerRepository.findById(id).map(customerMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Customer : {}", id);
        return customerRepository.deleteById(id).then(customerSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CustomerDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Customers for query {}", query);
        return customerSearchRepository.search(query, pageable).map(customerMapper::toDto);
    }
}
