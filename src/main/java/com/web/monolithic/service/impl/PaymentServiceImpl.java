package com.web.monolithic.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.web.monolithic.domain.Payment;
import com.web.monolithic.repository.PaymentRepository;
import com.web.monolithic.repository.search.PaymentSearchRepository;
import com.web.monolithic.service.PaymentService;
import com.web.monolithic.service.dto.PaymentDTO;
import com.web.monolithic.service.mapper.PaymentMapper;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Payment}.
 */
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    private final PaymentSearchRepository paymentSearchRepository;

    public PaymentServiceImpl(
        PaymentRepository paymentRepository,
        PaymentMapper paymentMapper,
        PaymentSearchRepository paymentSearchRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.paymentSearchRepository = paymentSearchRepository;
    }

    @Override
    public PaymentDTO save(PaymentDTO paymentDTO) {
        log.debug("Request to save Payment : {}", paymentDTO);
        Payment payment = paymentMapper.toEntity(paymentDTO);
        payment = paymentRepository.save(payment);
        PaymentDTO result = paymentMapper.toDto(payment);
        paymentSearchRepository.index(payment);
        return result;
    }

    @Override
    public PaymentDTO update(PaymentDTO paymentDTO) {
        log.debug("Request to update Payment : {}", paymentDTO);
        Payment payment = paymentMapper.toEntity(paymentDTO);
        payment = paymentRepository.save(payment);
        PaymentDTO result = paymentMapper.toDto(payment);
        paymentSearchRepository.index(payment);
        return result;
    }

    @Override
    public Optional<PaymentDTO> partialUpdate(PaymentDTO paymentDTO) {
        log.debug("Request to partially update Payment : {}", paymentDTO);

        return paymentRepository
            .findById(paymentDTO.getId())
            .map(existingPayment -> {
                paymentMapper.partialUpdate(existingPayment, paymentDTO);

                return existingPayment;
            })
            .map(paymentRepository::save)
            .map(savedPayment -> {
                paymentSearchRepository.save(savedPayment);

                return savedPayment;
            })
            .map(paymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Payments");
        return paymentRepository.findAll(pageable).map(paymentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDTO> findOne(UUID id) {
        log.debug("Request to get Payment : {}", id);
        return paymentRepository.findById(id).map(paymentMapper::toDto);
    }

    @Override
    public void delete(UUID id) {
        log.debug("Request to delete Payment : {}", id);
        paymentRepository.deleteById(id);
        paymentSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Payments for query {}", query);
        return paymentSearchRepository.search(query, pageable).map(paymentMapper::toDto);
    }
}
