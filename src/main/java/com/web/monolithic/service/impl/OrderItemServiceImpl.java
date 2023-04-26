package com.web.monolithic.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.web.monolithic.domain.OrderItem;
import com.web.monolithic.repository.OrderItemRepository;
import com.web.monolithic.repository.search.OrderItemSearchRepository;
import com.web.monolithic.service.OrderItemService;
import com.web.monolithic.service.dto.OrderItemDTO;
import com.web.monolithic.service.mapper.OrderItemMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link OrderItem}.
 */
@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final Logger log = LoggerFactory.getLogger(OrderItemServiceImpl.class);

    private final OrderItemRepository orderItemRepository;

    private final OrderItemMapper orderItemMapper;

    private final OrderItemSearchRepository orderItemSearchRepository;

    public OrderItemServiceImpl(
        OrderItemRepository orderItemRepository,
        OrderItemMapper orderItemMapper,
        OrderItemSearchRepository orderItemSearchRepository
    ) {
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
        this.orderItemSearchRepository = orderItemSearchRepository;
    }

    @Override
    public OrderItemDTO save(OrderItemDTO orderItemDTO) {
        log.debug("Request to save OrderItem : {}", orderItemDTO);
        OrderItem orderItem = orderItemMapper.toEntity(orderItemDTO);
        orderItem = orderItemRepository.save(orderItem);
        OrderItemDTO result = orderItemMapper.toDto(orderItem);
        orderItemSearchRepository.index(orderItem);
        return result;
    }

    @Override
    public OrderItemDTO update(OrderItemDTO orderItemDTO) {
        log.debug("Request to update OrderItem : {}", orderItemDTO);
        OrderItem orderItem = orderItemMapper.toEntity(orderItemDTO);
        orderItem = orderItemRepository.save(orderItem);
        OrderItemDTO result = orderItemMapper.toDto(orderItem);
        orderItemSearchRepository.index(orderItem);
        return result;
    }

    @Override
    public Optional<OrderItemDTO> partialUpdate(OrderItemDTO orderItemDTO) {
        log.debug("Request to partially update OrderItem : {}", orderItemDTO);

        return orderItemRepository
            .findById(orderItemDTO.getId())
            .map(existingOrderItem -> {
                orderItemMapper.partialUpdate(existingOrderItem, orderItemDTO);

                return existingOrderItem;
            })
            .map(orderItemRepository::save)
            .map(savedOrderItem -> {
                orderItemSearchRepository.save(savedOrderItem);

                return savedOrderItem;
            })
            .map(orderItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OrderItems");
        return orderItemRepository.findAll(pageable).map(orderItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderItemDTO> findOne(UUID id) {
        log.debug("Request to get OrderItem : {}", id);
        return orderItemRepository.findById(id).map(orderItemMapper::toDto);
    }

    @Override
    public void delete(UUID id) {
        log.debug("Request to delete OrderItem : {}", id);
        orderItemRepository.deleteById(id);
        orderItemSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OrderItems for query {}", query);
        return orderItemSearchRepository.search(query, pageable).map(orderItemMapper::toDto);
    }
}
