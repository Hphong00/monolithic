package com.web.monolithic.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.web.monolithic.domain.Order;
import com.web.monolithic.repository.OrderRepository;
import com.web.monolithic.repository.search.OrderSearchRepository;
import com.web.monolithic.service.OrderService;
import com.web.monolithic.service.dto.OrderDTO;
import com.web.monolithic.service.mapper.OrderMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Order}.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final OrderSearchRepository orderSearchRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderMapper orderMapper, OrderSearchRepository orderSearchRepository) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderSearchRepository = orderSearchRepository;
    }

    @Override
    public OrderDTO save(OrderDTO orderDTO) {
        log.debug("Request to save Order : {}", orderDTO);
        Order order = orderMapper.toEntity(orderDTO);
        order = orderRepository.save(order);
        OrderDTO result = orderMapper.toDto(order);
        orderSearchRepository.index(order);
        return result;
    }

    @Override
    public OrderDTO update(OrderDTO orderDTO) {
        log.debug("Request to update Order : {}", orderDTO);
        Order order = orderMapper.toEntity(orderDTO);
        order = orderRepository.save(order);
        OrderDTO result = orderMapper.toDto(order);
        orderSearchRepository.index(order);
        return result;
    }

    @Override
    public Optional<OrderDTO> partialUpdate(OrderDTO orderDTO) {
        log.debug("Request to partially update Order : {}", orderDTO);

        return orderRepository
            .findById(orderDTO.getId())
            .map(existingOrder -> {
                orderMapper.partialUpdate(existingOrder, orderDTO);

                return existingOrder;
            })
            .map(orderRepository::save)
            .map(savedOrder -> {
                orderSearchRepository.save(savedOrder);

                return savedOrder;
            })
            .map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Orders");
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDTO> findOne(UUID id) {
        log.debug("Request to get Order : {}", id);
        return orderRepository.findById(id).map(orderMapper::toDto);
    }

    @Override
    public void delete(UUID id) {
        log.debug("Request to delete Order : {}", id);
        orderRepository.deleteById(id);
        orderSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Orders for query {}", query);
        return orderSearchRepository.search(query, pageable).map(orderMapper::toDto);
    }
}
