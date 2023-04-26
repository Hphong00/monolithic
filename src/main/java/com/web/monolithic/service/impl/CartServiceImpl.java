package com.web.monolithic.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.web.monolithic.domain.Cart;
import com.web.monolithic.repository.CartRepository;
import com.web.monolithic.repository.search.CartSearchRepository;
import com.web.monolithic.service.CartService;
import com.web.monolithic.service.dto.CartDTO;
import com.web.monolithic.service.mapper.CartMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public CartDTO save(CartDTO cartDTO) {
        log.debug("Request to save Cart : {}", cartDTO);
        Cart cart = cartMapper.toEntity(cartDTO);
        cart = cartRepository.save(cart);
        CartDTO result = cartMapper.toDto(cart);
        cartSearchRepository.index(cart);
        return result;
    }

    @Override
    public CartDTO update(CartDTO cartDTO) {
        log.debug("Request to update Cart : {}", cartDTO);
        Cart cart = cartMapper.toEntity(cartDTO);
        cart = cartRepository.save(cart);
        CartDTO result = cartMapper.toDto(cart);
        cartSearchRepository.index(cart);
        return result;
    }

    @Override
    public Optional<CartDTO> partialUpdate(CartDTO cartDTO) {
        log.debug("Request to partially update Cart : {}", cartDTO);

        return cartRepository
            .findById(cartDTO.getId())
            .map(existingCart -> {
                cartMapper.partialUpdate(existingCart, cartDTO);

                return existingCart;
            })
            .map(cartRepository::save)
            .map(savedCart -> {
                cartSearchRepository.save(savedCart);

                return savedCart;
            })
            .map(cartMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CartDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Carts");
        return cartRepository.findAll(pageable).map(cartMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CartDTO> findOne(UUID id) {
        log.debug("Request to get Cart : {}", id);
        return cartRepository.findById(id).map(cartMapper::toDto);
    }

    @Override
    public void delete(UUID id) {
        log.debug("Request to delete Cart : {}", id);
        cartRepository.deleteById(id);
        cartSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CartDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Carts for query {}", query);
        return cartSearchRepository.search(query, pageable).map(cartMapper::toDto);
    }
}
