package com.web.monolithic.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.web.monolithic.IntegrationTest;
import com.web.monolithic.domain.Payment;
import com.web.monolithic.repository.PaymentRepository;
import com.web.monolithic.repository.search.PaymentSearchRepository;
import com.web.monolithic.service.dto.PaymentDTO;
import com.web.monolithic.service.mapper.PaymentMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PaymentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PaymentResourceIT {

    private static final UUID DEFAULT_USER_ID = UUID.randomUUID();
    private static final UUID UPDATED_USER_ID = UUID.randomUUID();

    private static final String DEFAULT_CARD_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CARD_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_CARD_HOLDER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CARD_HOLDER_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_CARD_EXPIRATION_MONTH = 1;
    private static final Integer UPDATED_CARD_EXPIRATION_MONTH = 2;

    private static final Integer DEFAULT_CARD_EXPIRATION_YEAR = 1;
    private static final Integer UPDATED_CARD_EXPIRATION_YEAR = 2;

    private static final String DEFAULT_CARD_CVV = "AAAAAAAAAA";
    private static final String UPDATED_CARD_CVV = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/payments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/payments";

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PaymentSearchRepository paymentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPaymentMockMvc;

    private Payment payment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createEntity(EntityManager em) {
        Payment payment = new Payment()
            .userId(DEFAULT_USER_ID)
            .cardNumber(DEFAULT_CARD_NUMBER)
            .cardHolderName(DEFAULT_CARD_HOLDER_NAME)
            .cardExpirationMonth(DEFAULT_CARD_EXPIRATION_MONTH)
            .cardExpirationYear(DEFAULT_CARD_EXPIRATION_YEAR)
            .cardCVV(DEFAULT_CARD_CVV)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return payment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Payment createUpdatedEntity(EntityManager em) {
        Payment payment = new Payment()
            .userId(UPDATED_USER_ID)
            .cardNumber(UPDATED_CARD_NUMBER)
            .cardHolderName(UPDATED_CARD_HOLDER_NAME)
            .cardExpirationMonth(UPDATED_CARD_EXPIRATION_MONTH)
            .cardExpirationYear(UPDATED_CARD_EXPIRATION_YEAR)
            .cardCVV(UPDATED_CARD_CVV)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        return payment;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        paymentSearchRepository.deleteAll();
        assertThat(paymentSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        payment = createEntity(em);
    }

    @Test
    @Transactional
    void createPayment() throws Exception {
        int databaseSizeBeforeCreate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);
        restPaymentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testPayment.getCardNumber()).isEqualTo(DEFAULT_CARD_NUMBER);
        assertThat(testPayment.getCardHolderName()).isEqualTo(DEFAULT_CARD_HOLDER_NAME);
        assertThat(testPayment.getCardExpirationMonth()).isEqualTo(DEFAULT_CARD_EXPIRATION_MONTH);
        assertThat(testPayment.getCardExpirationYear()).isEqualTo(DEFAULT_CARD_EXPIRATION_YEAR);
        assertThat(testPayment.getCardCVV()).isEqualTo(DEFAULT_CARD_CVV);
        assertThat(testPayment.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testPayment.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void createPaymentWithExistingId() throws Exception {
        // Create the Payment with an existing ID
        paymentRepository.saveAndFlush(payment);
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        int databaseSizeBeforeCreate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restPaymentMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllPayments() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get all the paymentList
        restPaymentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.toString())))
            .andExpect(jsonPath("$.[*].cardNumber").value(hasItem(DEFAULT_CARD_NUMBER)))
            .andExpect(jsonPath("$.[*].cardHolderName").value(hasItem(DEFAULT_CARD_HOLDER_NAME)))
            .andExpect(jsonPath("$.[*].cardExpirationMonth").value(hasItem(DEFAULT_CARD_EXPIRATION_MONTH)))
            .andExpect(jsonPath("$.[*].cardExpirationYear").value(hasItem(DEFAULT_CARD_EXPIRATION_YEAR)))
            .andExpect(jsonPath("$.[*].cardCVV").value(hasItem(DEFAULT_CARD_CVV)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getPayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        // Get the payment
        restPaymentMockMvc
            .perform(get(ENTITY_API_URL_ID, payment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(payment.getId().toString()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.toString()))
            .andExpect(jsonPath("$.cardNumber").value(DEFAULT_CARD_NUMBER))
            .andExpect(jsonPath("$.cardHolderName").value(DEFAULT_CARD_HOLDER_NAME))
            .andExpect(jsonPath("$.cardExpirationMonth").value(DEFAULT_CARD_EXPIRATION_MONTH))
            .andExpect(jsonPath("$.cardExpirationYear").value(DEFAULT_CARD_EXPIRATION_YEAR))
            .andExpect(jsonPath("$.cardCVV").value(DEFAULT_CARD_CVV))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPayment() throws Exception {
        // Get the payment
        restPaymentMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        paymentSearchRepository.save(payment);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());

        // Update the payment
        Payment updatedPayment = paymentRepository.findById(payment.getId()).get();
        // Disconnect from session so that the updates on updatedPayment are not directly saved in db
        em.detach(updatedPayment);
        updatedPayment
            .userId(UPDATED_USER_ID)
            .cardNumber(UPDATED_CARD_NUMBER)
            .cardHolderName(UPDATED_CARD_HOLDER_NAME)
            .cardExpirationMonth(UPDATED_CARD_EXPIRATION_MONTH)
            .cardExpirationYear(UPDATED_CARD_EXPIRATION_YEAR)
            .cardCVV(UPDATED_CARD_CVV)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        PaymentDTO paymentDTO = paymentMapper.toDto(updatedPayment);

        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testPayment.getCardNumber()).isEqualTo(UPDATED_CARD_NUMBER);
        assertThat(testPayment.getCardHolderName()).isEqualTo(UPDATED_CARD_HOLDER_NAME);
        assertThat(testPayment.getCardExpirationMonth()).isEqualTo(UPDATED_CARD_EXPIRATION_MONTH);
        assertThat(testPayment.getCardExpirationYear()).isEqualTo(UPDATED_CARD_EXPIRATION_YEAR);
        assertThat(testPayment.getCardCVV()).isEqualTo(UPDATED_CARD_CVV);
        assertThat(testPayment.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testPayment.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Payment> paymentSearchList = IterableUtils.toList(paymentSearchRepository.findAll());
                Payment testPaymentSearch = paymentSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testPaymentSearch.getUserId()).isEqualTo(UPDATED_USER_ID);
                assertThat(testPaymentSearch.getCardNumber()).isEqualTo(UPDATED_CARD_NUMBER);
                assertThat(testPaymentSearch.getCardHolderName()).isEqualTo(UPDATED_CARD_HOLDER_NAME);
                assertThat(testPaymentSearch.getCardExpirationMonth()).isEqualTo(UPDATED_CARD_EXPIRATION_MONTH);
                assertThat(testPaymentSearch.getCardExpirationYear()).isEqualTo(UPDATED_CARD_EXPIRATION_YEAR);
                assertThat(testPaymentSearch.getCardCVV()).isEqualTo(UPDATED_CARD_CVV);
                assertThat(testPaymentSearch.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
                assertThat(testPaymentSearch.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
            });
    }

    @Test
    @Transactional
    void putNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(UUID.randomUUID());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paymentDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(UUID.randomUUID());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(UUID.randomUUID());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment
            .cardNumber(UPDATED_CARD_NUMBER)
            .cardExpirationYear(UPDATED_CARD_EXPIRATION_YEAR)
            .createdAt(UPDATED_CREATED_AT);

        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            )
            .andExpect(status().isOk());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testPayment.getCardNumber()).isEqualTo(UPDATED_CARD_NUMBER);
        assertThat(testPayment.getCardHolderName()).isEqualTo(DEFAULT_CARD_HOLDER_NAME);
        assertThat(testPayment.getCardExpirationMonth()).isEqualTo(DEFAULT_CARD_EXPIRATION_MONTH);
        assertThat(testPayment.getCardExpirationYear()).isEqualTo(UPDATED_CARD_EXPIRATION_YEAR);
        assertThat(testPayment.getCardCVV()).isEqualTo(DEFAULT_CARD_CVV);
        assertThat(testPayment.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testPayment.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void fullUpdatePaymentWithPatch() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);

        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();

        // Update the payment using partial update
        Payment partialUpdatedPayment = new Payment();
        partialUpdatedPayment.setId(payment.getId());

        partialUpdatedPayment
            .userId(UPDATED_USER_ID)
            .cardNumber(UPDATED_CARD_NUMBER)
            .cardHolderName(UPDATED_CARD_HOLDER_NAME)
            .cardExpirationMonth(UPDATED_CARD_EXPIRATION_MONTH)
            .cardExpirationYear(UPDATED_CARD_EXPIRATION_YEAR)
            .cardCVV(UPDATED_CARD_CVV)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPayment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPayment))
            )
            .andExpect(status().isOk());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        Payment testPayment = paymentList.get(paymentList.size() - 1);
        assertThat(testPayment.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testPayment.getCardNumber()).isEqualTo(UPDATED_CARD_NUMBER);
        assertThat(testPayment.getCardHolderName()).isEqualTo(UPDATED_CARD_HOLDER_NAME);
        assertThat(testPayment.getCardExpirationMonth()).isEqualTo(UPDATED_CARD_EXPIRATION_MONTH);
        assertThat(testPayment.getCardExpirationYear()).isEqualTo(UPDATED_CARD_EXPIRATION_YEAR);
        assertThat(testPayment.getCardCVV()).isEqualTo(UPDATED_CARD_CVV);
        assertThat(testPayment.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testPayment.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(UUID.randomUUID());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paymentDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(UUID.randomUUID());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPayment() throws Exception {
        int databaseSizeBeforeUpdate = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        payment.setId(UUID.randomUUID());

        // Create the Payment
        PaymentDTO paymentDTO = paymentMapper.toDto(payment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPaymentMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paymentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Payment in the database
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deletePayment() throws Exception {
        // Initialize the database
        paymentRepository.saveAndFlush(payment);
        paymentRepository.save(payment);
        paymentSearchRepository.save(payment);

        int databaseSizeBeforeDelete = paymentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the payment
        restPaymentMockMvc
            .perform(delete(ENTITY_API_URL_ID, payment.getId().toString()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Payment> paymentList = paymentRepository.findAll();
        assertThat(paymentList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paymentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchPayment() throws Exception {
        // Initialize the database
        payment = paymentRepository.saveAndFlush(payment);
        paymentSearchRepository.save(payment);

        // Search the payment
        restPaymentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + payment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(payment.getId().toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.toString())))
            .andExpect(jsonPath("$.[*].cardNumber").value(hasItem(DEFAULT_CARD_NUMBER)))
            .andExpect(jsonPath("$.[*].cardHolderName").value(hasItem(DEFAULT_CARD_HOLDER_NAME)))
            .andExpect(jsonPath("$.[*].cardExpirationMonth").value(hasItem(DEFAULT_CARD_EXPIRATION_MONTH)))
            .andExpect(jsonPath("$.[*].cardExpirationYear").value(hasItem(DEFAULT_CARD_EXPIRATION_YEAR)))
            .andExpect(jsonPath("$.[*].cardCVV").value(hasItem(DEFAULT_CARD_CVV)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }
}
