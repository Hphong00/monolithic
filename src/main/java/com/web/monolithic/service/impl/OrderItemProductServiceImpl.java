package com.web.monolithic.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.web.monolithic.domain.OrderItemProduct;
import com.web.monolithic.repository.OrderItemProductRepository;
import com.web.monolithic.repository.search.OrderItemProductSearchRepository;
import com.web.monolithic.service.OrderItemProductService;
import com.web.monolithic.service.dto.OrderItemProductDTO;
import com.web.monolithic.service.mapper.OrderItemProductMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link OrderItemProduct}.
 */
@Service
@Transactional
public class OrderItemProductServiceImpl implements OrderItemProductService {

    private final Logger log = LoggerFactory.getLogger(OrderItemProductServiceImpl.class);

    private final OrderItemProductRepository orderItemProductRepository;

    private final OrderItemProductMapper orderItemProductMapper;

    private final OrderItemProductSearchRepository orderItemProductSearchRepository;

    public OrderItemProductServiceImpl(
        OrderItemProductRepository orderItemProductRepository,
        OrderItemProductMapper orderItemProductMapper,
        OrderItemProductSearchRepository orderItemProductSearchRepository
    ) {
        this.orderItemProductRepository = orderItemProductRepository;
        this.orderItemProductMapper = orderItemProductMapper;
        this.orderItemProductSearchRepository = orderItemProductSearchRepository;
    }

    @Override
    public OrderItemProductDTO save(OrderItemProductDTO orderItemProductDTO) {
        log.debug("Request to save OrderItemProduct : {}", orderItemProductDTO);
        OrderItemProduct orderItemProduct = orderItemProductMapper.toEntity(orderItemProductDTO);
        orderItemProduct = orderItemProductRepository.save(orderItemProduct);
        OrderItemProductDTO result = orderItemProductMapper.toDto(orderItemProduct);
        orderItemProductSearchRepository.index(orderItemProduct);
        return result;
    }

    @Override
    public OrderItemProductDTO update(OrderItemProductDTO orderItemProductDTO) {
        log.debug("Request to update OrderItemProduct : {}", orderItemProductDTO);
        OrderItemProduct orderItemProduct = orderItemProductMapper.toEntity(orderItemProductDTO);
        orderItemProduct = orderItemProductRepository.save(orderItemProduct);
        OrderItemProductDTO result = orderItemProductMapper.toDto(orderItemProduct);
        orderItemProductSearchRepository.index(orderItemProduct);
        return result;
    }

    @Override
    public Optional<OrderItemProductDTO> partialUpdate(OrderItemProductDTO orderItemProductDTO) {
        log.debug("Request to partially update OrderItemProduct : {}", orderItemProductDTO);

        return orderItemProductRepository
            .findById(orderItemProductDTO.getId())
            .map(existingOrderItemProduct -> {
                orderItemProductMapper.partialUpdate(existingOrderItemProduct, orderItemProductDTO);

                return existingOrderItemProduct;
            })
            .map(orderItemProductRepository::save)
            .map(savedOrderItemProduct -> {
                orderItemProductSearchRepository.save(savedOrderItemProduct);

                return savedOrderItemProduct;
            })
            .map(orderItemProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemProductDTO> findAll(Pageable pageable) {
        log.debug("Request to get all OrderItemProducts");
        return orderItemProductRepository.findAll(pageable).map(orderItemProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderItemProductDTO> findOne(UUID id) {
        log.debug("Request to get OrderItemProduct : {}", id);
        return orderItemProductRepository.findById(id).map(orderItemProductMapper::toDto);
    }

    @Override
    public void delete(UUID id) {
        log.debug("Request to delete OrderItemProduct : {}", id);
        orderItemProductRepository.deleteById(id);
        orderItemProductSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderItemProductDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of OrderItemProducts for query {}", query);
        return orderItemProductSearchRepository.search(query, pageable).map(orderItemProductMapper::toDto);
    }
}
