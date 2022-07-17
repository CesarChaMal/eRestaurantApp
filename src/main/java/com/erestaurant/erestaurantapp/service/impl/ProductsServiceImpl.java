package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Products;
import com.erestaurant.erestaurantapp.repository.ProductsRepository;
import com.erestaurant.erestaurantapp.repository.search.ProductsSearchRepository;
import com.erestaurant.erestaurantapp.service.ProductsService;
import com.erestaurant.erestaurantapp.service.dto.ProductsDTO;
import com.erestaurant.erestaurantapp.service.mapper.ProductsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Products}.
 */
@Service
@Transactional
public class ProductsServiceImpl implements ProductsService {

    private final Logger log = LoggerFactory.getLogger(ProductsServiceImpl.class);

    private final ProductsRepository productsRepository;

    private final ProductsMapper productsMapper;

    private final ProductsSearchRepository productsSearchRepository;

    public ProductsServiceImpl(
        ProductsRepository productsRepository,
        ProductsMapper productsMapper,
        ProductsSearchRepository productsSearchRepository
    ) {
        this.productsRepository = productsRepository;
        this.productsMapper = productsMapper;
        this.productsSearchRepository = productsSearchRepository;
    }

    @Override
    public Mono<ProductsDTO> save(ProductsDTO productsDTO) {
        log.debug("Request to save Products : {}", productsDTO);
        return productsRepository
            .save(productsMapper.toEntity(productsDTO))
            .flatMap(productsSearchRepository::save)
            .map(productsMapper::toDto);
    }

    @Override
    public Mono<ProductsDTO> update(ProductsDTO productsDTO) {
        log.debug("Request to save Products : {}", productsDTO);
        return productsRepository
            .save(productsMapper.toEntity(productsDTO).setIsPersisted())
            .flatMap(productsSearchRepository::save)
            .map(productsMapper::toDto);
    }

    @Override
    public Mono<ProductsDTO> partialUpdate(ProductsDTO productsDTO) {
        log.debug("Request to partially update Products : {}", productsDTO);

        return productsRepository
            .findById(productsDTO.getId())
            .map(existingProducts -> {
                productsMapper.partialUpdate(existingProducts, productsDTO);

                return existingProducts;
            })
            .flatMap(productsRepository::save)
            .flatMap(savedProducts -> {
                productsSearchRepository.save(savedProducts);

                return Mono.just(savedProducts);
            })
            .map(productsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProductsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Products");
        return productsRepository.findAllBy(pageable).map(productsMapper::toDto);
    }

    public Mono<Long> countAll() {
        return productsRepository.count();
    }

    public Mono<Long> searchCount() {
        return productsSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProductsDTO> findOne(String id) {
        log.debug("Request to get Products : {}", id);
        return productsRepository.findById(id).map(productsMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Products : {}", id);
        return productsRepository.deleteById(id).then(productsSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProductsDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Products for query {}", query);
        return productsSearchRepository.search(query, pageable).map(productsMapper::toDto);
    }
}
