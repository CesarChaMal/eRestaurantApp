package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Cart;
import com.erestaurant.erestaurantapp.repository.CartRepository;
import com.erestaurant.erestaurantapp.repository.search.CartSearchRepository;
import com.erestaurant.erestaurantapp.service.CartService;
import com.erestaurant.erestaurantapp.service.dto.CartDTO;
import com.erestaurant.erestaurantapp.service.mapper.CartMapper;
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
 * Service Implementation for managing {@link Cart}.
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;

    private final CartMapper cartMapper;

    private final CartSearchRepository cartSearchRepository;

    public CartServiceImpl(CartRepository cartRepository, CartMapper cartMapper, CartSearchRepository cartSearchRepository) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
        this.cartSearchRepository = cartSearchRepository;
    }

    @Override
    public Mono<CartDTO> save(CartDTO cartDTO) {
        log.debug("Request to save Cart : {}", cartDTO);
        return cartRepository.save(cartMapper.toEntity(cartDTO)).flatMap(cartSearchRepository::save).map(cartMapper::toDto);
    }

    @Override
    public Mono<CartDTO> update(CartDTO cartDTO) {
        log.debug("Request to save Cart : {}", cartDTO);
        return cartRepository
            .save(cartMapper.toEntity(cartDTO).setIsPersisted())
            .flatMap(cartSearchRepository::save)
            .map(cartMapper::toDto);
    }

    @Override
    public Mono<CartDTO> partialUpdate(CartDTO cartDTO) {
        log.debug("Request to partially update Cart : {}", cartDTO);

        return cartRepository
            .findById(cartDTO.getId())
            .map(existingCart -> {
                cartMapper.partialUpdate(existingCart, cartDTO);

                return existingCart;
            })
            .flatMap(cartRepository::save)
            .flatMap(savedCart -> {
                cartSearchRepository.save(savedCart);

                return Mono.just(savedCart);
            })
            .map(cartMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CartDTO> findAll() {
        log.debug("Request to get all Carts");
        return cartRepository.findAll().map(cartMapper::toDto);
    }

    public Mono<Long> countAll() {
        return cartRepository.count();
    }

    public Mono<Long> searchCount() {
        return cartSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CartDTO> findOne(String id) {
        log.debug("Request to get Cart : {}", id);
        return cartRepository.findById(id).map(cartMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Cart : {}", id);
        return cartRepository.deleteById(id).then(cartSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CartDTO> search(String query) {
        log.debug("Request to search Carts for query {}", query);
        return cartSearchRepository.search(query).map(cartMapper::toDto);
    }
}
