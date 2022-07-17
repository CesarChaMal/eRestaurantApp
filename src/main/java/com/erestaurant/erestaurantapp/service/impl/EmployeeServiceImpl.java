package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Employee;
import com.erestaurant.erestaurantapp.repository.EmployeeRepository;
import com.erestaurant.erestaurantapp.repository.search.EmployeeSearchRepository;
import com.erestaurant.erestaurantapp.service.EmployeeService;
import com.erestaurant.erestaurantapp.service.dto.EmployeeDTO;
import com.erestaurant.erestaurantapp.service.mapper.EmployeeMapper;
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
 * Service Implementation for managing {@link Employee}.
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    private final EmployeeSearchRepository employeeSearchRepository;

    public EmployeeServiceImpl(
        EmployeeRepository employeeRepository,
        EmployeeMapper employeeMapper,
        EmployeeSearchRepository employeeSearchRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.employeeSearchRepository = employeeSearchRepository;
    }

    @Override
    public Mono<EmployeeDTO> save(EmployeeDTO employeeDTO) {
        log.debug("Request to save Employee : {}", employeeDTO);
        return employeeRepository
            .save(employeeMapper.toEntity(employeeDTO))
            .flatMap(employeeSearchRepository::save)
            .map(employeeMapper::toDto);
    }

    @Override
    public Mono<EmployeeDTO> update(EmployeeDTO employeeDTO) {
        log.debug("Request to save Employee : {}", employeeDTO);
        return employeeRepository
            .save(employeeMapper.toEntity(employeeDTO).setIsPersisted())
            .flatMap(employeeSearchRepository::save)
            .map(employeeMapper::toDto);
    }

    @Override
    public Mono<EmployeeDTO> partialUpdate(EmployeeDTO employeeDTO) {
        log.debug("Request to partially update Employee : {}", employeeDTO);

        return employeeRepository
            .findById(employeeDTO.getId())
            .map(existingEmployee -> {
                employeeMapper.partialUpdate(existingEmployee, employeeDTO);

                return existingEmployee;
            })
            .flatMap(employeeRepository::save)
            .flatMap(savedEmployee -> {
                employeeSearchRepository.save(savedEmployee);

                return Mono.just(savedEmployee);
            })
            .map(employeeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EmployeeDTO> findAll() {
        log.debug("Request to get all Employees");
        return employeeRepository.findAll().map(employeeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return employeeRepository.count();
    }

    public Mono<Long> searchCount() {
        return employeeSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EmployeeDTO> findOne(String id) {
        log.debug("Request to get Employee : {}", id);
        return employeeRepository.findById(id).map(employeeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Employee : {}", id);
        return employeeRepository.deleteById(id).then(employeeSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EmployeeDTO> search(String query) {
        log.debug("Request to search Employees for query {}", query);
        return employeeSearchRepository.search(query).map(employeeMapper::toDto);
    }
}
