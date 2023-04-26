package com.web.monolithic.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.web.monolithic.domain.CartItem;
import com.web.monolithic.repository.CartItemRepository;
import com.web.monolithic.repository.search.CartItemSearchRepository;
import com.web.monolithic.service.CartItemService;
import com.web.monolithic.service.dto.CartItemDTO;
import com.web.monolithic.service.mapper.CartItemMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link CartItem}.
 */
@Service
@Transactional
public class CartItemServiceImpl implements CartItemService {

    private final Logger log = LoggerFactory.getLogger(CartItemServiceImpl.class);

    private final CartItemRepository cartItemRepository;

    private final CartItemMapper cartItemMapper;

    private final CartItemSearchRepository cartItemSearchRepository;

    public CartItemServiceImpl(
        CartItemRepository cartItemRepository,
        CartItemMapper cartItemMapper,
        CartItemSearchRepository cartItemSearchRepository
    ) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemMapper = cartItemMapper;
        this.cartItemSearchRepository = cartItemSearchRepository;
    }

    @Override
    public CartItemDTO save(CartItemDTO cartItemDTO) {
        log.debug("Request to save CartItem : {}", cartItemDTO);
        CartItem cartItem = cartItemMapper.toEntity(cartItemDTO);
        cartItem = cartItemRepository.save(cartItem);
        CartItemDTO result = cartItemMapper.toDto(cartItem);
        cartItemSearchRepository.index(cartItem);
        return result;
    }

    @Override
    public CartItemDTO update(CartItemDTO cartItemDTO) {
        log.debug("Request to update CartItem : {}", cartItemDTO);
        CartItem cartItem = cartItemMapper.toEntity(cartItemDTO);
        cartItem = cartItemRepository.save(cartItem);
        CartItemDTO result = cartItemMapper.toDto(cartItem);
        cartItemSearchRepository.index(cartItem);
        return result;
    }

    @Override
    public Optional<CartItemDTO> partialUpdate(CartItemDTO cartItemDTO) {
        log.debug("Request to partially update CartItem : {}", cartItemDTO);

        return cartItemRepository
            .findById(cartItemDTO.getId())
            .map(existingCartItem -> {
                cartItemMapper.partialUpdate(existingCartItem, cartItemDTO);

                return existingCartItem;
            })
            .map(cartItemRepository::save)
            .map(savedCartItem -> {
                cartItemSearchRepository.save(savedCartItem);

                return savedCartItem;
            })
            .map(cartItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CartItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CartItems");
        return cartItemRepository.findAll(pageable).map(cartItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CartItemDTO> findOne(UUID id) {
        log.debug("Request to get CartItem : {}", id);
        return cartItemRepository.findById(id).map(cartItemMapper::toDto);
    }

    @Override
    public void delete(UUID id) {
        log.debug("Request to delete CartItem : {}", id);
        cartItemRepository.deleteById(id);
        cartItemSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CartItemDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of CartItems for query {}", query);
        return cartItemSearchRepository.search(query, pageable).map(cartItemMapper::toDto);
    }
}
