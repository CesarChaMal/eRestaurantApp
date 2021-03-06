package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Discount;
import com.erestaurant.erestaurantapp.repository.DiscountRepository;
import com.erestaurant.erestaurantapp.repository.search.DiscountSearchRepository;
import com.erestaurant.erestaurantapp.service.DiscountService;
import com.erestaurant.erestaurantapp.service.dto.DiscountDTO;
import com.erestaurant.erestaurantapp.service.mapper.DiscountMapper;
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
 * Service Implementation for managing {@link Discount}.
 */
@Service
@Transactional
public class DiscountServiceImpl implements DiscountService {

    private final Logger log = LoggerFactory.getLogger(DiscountServiceImpl.class);

    private final DiscountRepository discountRepository;

    private final DiscountMapper discountMapper;

    private final DiscountSearchRepository discountSearchRepository;

    public DiscountServiceImpl(
        DiscountRepository discountRepository,
        DiscountMapper discountMapper,
        DiscountSearchRepository discountSearchRepository
    ) {
        this.discountRepository = discountRepository;
        this.discountMapper = discountMapper;
        this.discountSearchRepository = discountSearchRepository;
    }

    @Override
    public Mono<DiscountDTO> save(DiscountDTO discountDTO) {
        log.debug("Request to save Discount : {}", discountDTO);
        return discountRepository
            .save(discountMapper.toEntity(discountDTO))
            .flatMap(discountSearchRepository::save)
            .map(discountMapper::toDto);
    }

    @Override
    public Mono<DiscountDTO> update(DiscountDTO discountDTO) {
        log.debug("Request to save Discount : {}", discountDTO);
        return discountRepository
            .save(discountMapper.toEntity(discountDTO).setIsPersisted())
            .flatMap(discountSearchRepository::save)
            .map(discountMapper::toDto);
    }

    @Override
    public Mono<DiscountDTO> partialUpdate(DiscountDTO discountDTO) {
        log.debug("Request to partially update Discount : {}", discountDTO);

        return discountRepository
            .findById(discountDTO.getId())
            .map(existingDiscount -> {
                discountMapper.partialUpdate(existingDiscount, discountDTO);

                return existingDiscount;
            })
            .flatMap(discountRepository::save)
            .flatMap(savedDiscount -> {
                discountSearchRepository.save(savedDiscount);

                return Mono.just(savedDiscount);
            })
            .map(discountMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DiscountDTO> findAll() {
        log.debug("Request to get all Discounts");
        return discountRepository.findAll().map(discountMapper::toDto);
    }

    public Mono<Long> countAll() {
        return discountRepository.count();
    }

    public Mono<Long> searchCount() {
        return discountSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<DiscountDTO> findOne(String id) {
        log.debug("Request to get Discount : {}", id);
        return discountRepository.findById(id).map(discountMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Discount : {}", id);
        return discountRepository.deleteById(id).then(discountSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<DiscountDTO> search(String query) {
        log.debug("Request to search Discounts for query {}", query);
        return discountSearchRepository.search(query).map(discountMapper::toDto);
    }
}
