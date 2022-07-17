package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Categories;
import com.erestaurant.erestaurantapp.repository.CategoriesRepository;
import com.erestaurant.erestaurantapp.repository.search.CategoriesSearchRepository;
import com.erestaurant.erestaurantapp.service.CategoriesService;
import com.erestaurant.erestaurantapp.service.dto.CategoriesDTO;
import com.erestaurant.erestaurantapp.service.mapper.CategoriesMapper;
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
 * Service Implementation for managing {@link Categories}.
 */
@Service
@Transactional
public class CategoriesServiceImpl implements CategoriesService {

    private final Logger log = LoggerFactory.getLogger(CategoriesServiceImpl.class);

    private final CategoriesRepository categoriesRepository;

    private final CategoriesMapper categoriesMapper;

    private final CategoriesSearchRepository categoriesSearchRepository;

    public CategoriesServiceImpl(
        CategoriesRepository categoriesRepository,
        CategoriesMapper categoriesMapper,
        CategoriesSearchRepository categoriesSearchRepository
    ) {
        this.categoriesRepository = categoriesRepository;
        this.categoriesMapper = categoriesMapper;
        this.categoriesSearchRepository = categoriesSearchRepository;
    }

    @Override
    public Mono<CategoriesDTO> save(CategoriesDTO categoriesDTO) {
        log.debug("Request to save Categories : {}", categoriesDTO);
        return categoriesRepository
            .save(categoriesMapper.toEntity(categoriesDTO))
            .flatMap(categoriesSearchRepository::save)
            .map(categoriesMapper::toDto);
    }

    @Override
    public Mono<CategoriesDTO> update(CategoriesDTO categoriesDTO) {
        log.debug("Request to save Categories : {}", categoriesDTO);
        return categoriesRepository
            .save(categoriesMapper.toEntity(categoriesDTO).setIsPersisted())
            .flatMap(categoriesSearchRepository::save)
            .map(categoriesMapper::toDto);
    }

    @Override
    public Mono<CategoriesDTO> partialUpdate(CategoriesDTO categoriesDTO) {
        log.debug("Request to partially update Categories : {}", categoriesDTO);

        return categoriesRepository
            .findById(categoriesDTO.getId())
            .map(existingCategories -> {
                categoriesMapper.partialUpdate(existingCategories, categoriesDTO);

                return existingCategories;
            })
            .flatMap(categoriesRepository::save)
            .flatMap(savedCategories -> {
                categoriesSearchRepository.save(savedCategories);

                return Mono.just(savedCategories);
            })
            .map(categoriesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CategoriesDTO> findAll() {
        log.debug("Request to get all Categories");
        return categoriesRepository.findAll().map(categoriesMapper::toDto);
    }

    public Mono<Long> countAll() {
        return categoriesRepository.count();
    }

    public Mono<Long> searchCount() {
        return categoriesSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CategoriesDTO> findOne(String id) {
        log.debug("Request to get Categories : {}", id);
        return categoriesRepository.findById(id).map(categoriesMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Categories : {}", id);
        return categoriesRepository.deleteById(id).then(categoriesSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CategoriesDTO> search(String query) {
        log.debug("Request to search Categories for query {}", query);
        return categoriesSearchRepository.search(query).map(categoriesMapper::toDto);
    }
}
