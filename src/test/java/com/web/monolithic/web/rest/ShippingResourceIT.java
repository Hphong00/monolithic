package com.web.monolithic.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.web.monolithic.IntegrationTest;
import com.web.monolithic.domain.Shipping;
import com.web.monolithic.repository.ShippingRepository;
import com.web.monolithic.repository.search.ShippingSearchRepository;
import com.web.monolithic.service.dto.ShippingDTO;
import com.web.monolithic.service.mapper.ShippingMapper;
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
 * Integration tests for the {@link ShippingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShippingResourceIT {

    private static final UUID DEFAULT_USER_ID = UUID.randomUUID();
    private static final UUID UPDATED_USER_ID = UUID.randomUUID();

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_STATE = "AAAAAAAAAA";
    private static final String UPDATED_STATE = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String DEFAULT_POSTAL_CODE = "AAAAAAAAAA";
    private static final String UPDATED_POSTAL_CODE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/shippings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/shippings";

    @Autowired
    private ShippingRepository shippingRepository;

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private ShippingSearchRepository shippingSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShippingMockMvc;

    private Shipping shipping;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shipping createEntity(EntityManager em) {
        Shipping shipping = new Shipping()
            .userId(DEFAULT_USER_ID)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .phone(DEFAULT_PHONE)
            .address(DEFAULT_ADDRESS)
            .city(DEFAULT_CITY)
            .state(DEFAULT_STATE)
            .country(DEFAULT_COUNTRY)
            .postalCode(DEFAULT_POSTAL_CODE)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);
        return shipping;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shipping createUpdatedEntity(EntityManager em) {
        Shipping shipping = new Shipping()
            .userId(UPDATED_USER_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .country(UPDATED_COUNTRY)
            .postalCode(UPDATED_POSTAL_CODE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        return shipping;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        shippingSearchRepository.deleteAll();
        assertThat(shippingSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        shipping = createEntity(em);
    }

    @Test
    @Transactional
    void createShipping() throws Exception {
        int databaseSizeBeforeCreate = shippingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        // Create the Shipping
        ShippingDTO shippingDTO = shippingMapper.toDto(shipping);
        restShippingMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shippingDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Shipping in the database
        List<Shipping> shippingList = shippingRepository.findAll();
        assertThat(shippingList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Shipping testShipping = shippingList.get(shippingList.size() - 1);
        assertThat(testShipping.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testShipping.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testShipping.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testShipping.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testShipping.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testShipping.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testShipping.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testShipping.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testShipping.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testShipping.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testShipping.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void createShippingWithExistingId() throws Exception {
        // Create the Shipping with an existing ID
        shippingRepository.saveAndFlush(shipping);
        ShippingDTO shippingDTO = shippingMapper.toDto(shipping);

        int databaseSizeBeforeCreate = shippingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restShippingMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shippingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shipping in the database
        List<Shipping> shippingList = shippingRepository.findAll();
        assertThat(shippingList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllShippings() throws Exception {
        // Initialize the database
        shippingRepository.saveAndFlush(shipping);

        // Get all the shippingList
        restShippingMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipping.getId().toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.toString())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getShipping() throws Exception {
        // Initialize the database
        shippingRepository.saveAndFlush(shipping);

        // Get the shipping
        restShippingMockMvc
            .perform(get(ENTITY_API_URL_ID, shipping.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shipping.getId().toString()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.toString()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY))
            .andExpect(jsonPath("$.postalCode").value(DEFAULT_POSTAL_CODE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingShipping() throws Exception {
        // Get the shipping
        restShippingMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShipping() throws Exception {
        // Initialize the database
        shippingRepository.saveAndFlush(shipping);

        int databaseSizeBeforeUpdate = shippingRepository.findAll().size();
        shippingSearchRepository.save(shipping);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingSearchRepository.findAll());

        // Update the shipping
        Shipping updatedShipping = shippingRepository.findById(shipping.getId()).get();
        // Disconnect from session so that the updates on updatedShipping are not directly saved in db
        em.detach(updatedShipping);
        updatedShipping
            .userId(UPDATED_USER_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .country(UPDATED_COUNTRY)
            .postalCode(UPDATED_POSTAL_CODE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        ShippingDTO shippingDTO = shippingMapper.toDto(updatedShipping);

        restShippingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shippingDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shippingDTO))
            )
            .andExpect(status().isOk());

        // Validate the Shipping in the database
        List<Shipping> shippingList = shippingRepository.findAll();
        assertThat(shippingList).hasSize(databaseSizeBeforeUpdate);
        Shipping testShipping = shippingList.get(shippingList.size() - 1);
        assertThat(testShipping.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testShipping.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testShipping.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testShipping.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testShipping.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testShipping.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testShipping.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testShipping.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testShipping.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testShipping.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testShipping.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Shipping> shippingSearchList = IterableUtils.toList(shippingSearchRepository.findAll());
                Shipping testShippingSearch = shippingSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testShippingSearch.getUserId()).isEqualTo(UPDATED_USER_ID);
                assertThat(testShippingSearch.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
                assertThat(testShippingSearch.getLastName()).isEqualTo(UPDATED_LAST_NAME);
                assertThat(testShippingSearch.getPhone()).isEqualTo(UPDATED_PHONE);
                assertThat(testShippingSearch.getAddress()).isEqualTo(UPDATED_ADDRESS);
                assertThat(testShippingSearch.getCity()).isEqualTo(UPDATED_CITY);
                assertThat(testShippingSearch.getState()).isEqualTo(UPDATED_STATE);
                assertThat(testShippingSearch.getCountry()).isEqualTo(UPDATED_COUNTRY);
                assertThat(testShippingSearch.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
                assertThat(testShippingSearch.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
                assertThat(testShippingSearch.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
            });
    }

    @Test
    @Transactional
    void putNonExistingShipping() throws Exception {
        int databaseSizeBeforeUpdate = shippingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        shipping.setId(UUID.randomUUID());

        // Create the Shipping
        ShippingDTO shippingDTO = shippingMapper.toDto(shipping);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShippingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shippingDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shippingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shipping in the database
        List<Shipping> shippingList = shippingRepository.findAll();
        assertThat(shippingList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchShipping() throws Exception {
        int databaseSizeBeforeUpdate = shippingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        shipping.setId(UUID.randomUUID());

        // Create the Shipping
        ShippingDTO shippingDTO = shippingMapper.toDto(shipping);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShippingMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shippingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shipping in the database
        List<Shipping> shippingList = shippingRepository.findAll();
        assertThat(shippingList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShipping() throws Exception {
        int databaseSizeBeforeUpdate = shippingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        shipping.setId(UUID.randomUUID());

        // Create the Shipping
        ShippingDTO shippingDTO = shippingMapper.toDto(shipping);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShippingMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shippingDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shipping in the database
        List<Shipping> shippingList = shippingRepository.findAll();
        assertThat(shippingList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateShippingWithPatch() throws Exception {
        // Initialize the database
        shippingRepository.saveAndFlush(shipping);

        int databaseSizeBeforeUpdate = shippingRepository.findAll().size();

        // Update the shipping using partial update
        Shipping partialUpdatedShipping = new Shipping();
        partialUpdatedShipping.setId(shipping.getId());

        partialUpdatedShipping.userId(UPDATED_USER_ID).city(UPDATED_CITY);

        restShippingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipping.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShipping))
            )
            .andExpect(status().isOk());

        // Validate the Shipping in the database
        List<Shipping> shippingList = shippingRepository.findAll();
        assertThat(shippingList).hasSize(databaseSizeBeforeUpdate);
        Shipping testShipping = shippingList.get(shippingList.size() - 1);
        assertThat(testShipping.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testShipping.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testShipping.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testShipping.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testShipping.getAddress()).isEqualTo(DEFAULT_ADDRESS);
        assertThat(testShipping.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testShipping.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testShipping.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testShipping.getPostalCode()).isEqualTo(DEFAULT_POSTAL_CODE);
        assertThat(testShipping.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testShipping.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateShippingWithPatch() throws Exception {
        // Initialize the database
        shippingRepository.saveAndFlush(shipping);

        int databaseSizeBeforeUpdate = shippingRepository.findAll().size();

        // Update the shipping using partial update
        Shipping partialUpdatedShipping = new Shipping();
        partialUpdatedShipping.setId(shipping.getId());

        partialUpdatedShipping
            .userId(UPDATED_USER_ID)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .country(UPDATED_COUNTRY)
            .postalCode(UPDATED_POSTAL_CODE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restShippingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShipping.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShipping))
            )
            .andExpect(status().isOk());

        // Validate the Shipping in the database
        List<Shipping> shippingList = shippingRepository.findAll();
        assertThat(shippingList).hasSize(databaseSizeBeforeUpdate);
        Shipping testShipping = shippingList.get(shippingList.size() - 1);
        assertThat(testShipping.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testShipping.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testShipping.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testShipping.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testShipping.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testShipping.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testShipping.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testShipping.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testShipping.getPostalCode()).isEqualTo(UPDATED_POSTAL_CODE);
        assertThat(testShipping.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testShipping.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingShipping() throws Exception {
        int databaseSizeBeforeUpdate = shippingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        shipping.setId(UUID.randomUUID());

        // Create the Shipping
        ShippingDTO shippingDTO = shippingMapper.toDto(shipping);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShippingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shippingDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shippingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shipping in the database
        List<Shipping> shippingList = shippingRepository.findAll();
        assertThat(shippingList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShipping() throws Exception {
        int databaseSizeBeforeUpdate = shippingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        shipping.setId(UUID.randomUUID());

        // Create the Shipping
        ShippingDTO shippingDTO = shippingMapper.toDto(shipping);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShippingMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shippingDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shipping in the database
        List<Shipping> shippingList = shippingRepository.findAll();
        assertThat(shippingList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShipping() throws Exception {
        int databaseSizeBeforeUpdate = shippingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        shipping.setId(UUID.randomUUID());

        // Create the Shipping
        ShippingDTO shippingDTO = shippingMapper.toDto(shipping);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShippingMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shippingDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shipping in the database
        List<Shipping> shippingList = shippingRepository.findAll();
        assertThat(shippingList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteShipping() throws Exception {
        // Initialize the database
        shippingRepository.saveAndFlush(shipping);
        shippingRepository.save(shipping);
        shippingSearchRepository.save(shipping);

        int databaseSizeBeforeDelete = shippingRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the shipping
        restShippingMockMvc
            .perform(delete(ENTITY_API_URL_ID, shipping.getId().toString()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Shipping> shippingList = shippingRepository.findAll();
        assertThat(shippingList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(shippingSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchShipping() throws Exception {
        // Initialize the database
        shipping = shippingRepository.saveAndFlush(shipping);
        shippingSearchRepository.save(shipping);

        // Search the shipping
        restShippingMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + shipping.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shipping.getId().toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.toString())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].postalCode").value(hasItem(DEFAULT_POSTAL_CODE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }
}
