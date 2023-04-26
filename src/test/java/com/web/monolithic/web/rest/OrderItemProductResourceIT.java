package com.web.monolithic.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.web.monolithic.IntegrationTest;
import com.web.monolithic.domain.OrderItemProduct;
import com.web.monolithic.repository.OrderItemProductRepository;
import com.web.monolithic.repository.search.OrderItemProductSearchRepository;
import com.web.monolithic.service.dto.OrderItemProductDTO;
import com.web.monolithic.service.mapper.OrderItemProductMapper;
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
 * Integration tests for the {@link OrderItemProductResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OrderItemProductResourceIT {

    private static final UUID DEFAULT_ORDER_ITEM_ID = UUID.randomUUID();
    private static final UUID UPDATED_ORDER_ITEM_ID = UUID.randomUUID();

    private static final UUID DEFAULT_PRODUCT_ID = UUID.randomUUID();
    private static final UUID UPDATED_PRODUCT_ID = UUID.randomUUID();

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final String ENTITY_API_URL = "/api/order-item-products";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/order-item-products";

    @Autowired
    private OrderItemProductRepository orderItemProductRepository;

    @Autowired
    private OrderItemProductMapper orderItemProductMapper;

    @Autowired
    private OrderItemProductSearchRepository orderItemProductSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderItemProductMockMvc;

    private OrderItemProduct orderItemProduct;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItemProduct createEntity(EntityManager em) {
        OrderItemProduct orderItemProduct = new OrderItemProduct()
            .orderItemId(DEFAULT_ORDER_ITEM_ID)
            .productId(DEFAULT_PRODUCT_ID)
            .quantity(DEFAULT_QUANTITY);
        return orderItemProduct;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderItemProduct createUpdatedEntity(EntityManager em) {
        OrderItemProduct orderItemProduct = new OrderItemProduct()
            .orderItemId(UPDATED_ORDER_ITEM_ID)
            .productId(UPDATED_PRODUCT_ID)
            .quantity(UPDATED_QUANTITY);
        return orderItemProduct;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        orderItemProductSearchRepository.deleteAll();
        assertThat(orderItemProductSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        orderItemProduct = createEntity(em);
    }

    @Test
    @Transactional
    void createOrderItemProduct() throws Exception {
        int databaseSizeBeforeCreate = orderItemProductRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        // Create the OrderItemProduct
        OrderItemProductDTO orderItemProductDTO = orderItemProductMapper.toDto(orderItemProduct);
        restOrderItemProductMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderItemProductDTO))
            )
            .andExpect(status().isCreated());

        // Validate the OrderItemProduct in the database
        List<OrderItemProduct> orderItemProductList = orderItemProductRepository.findAll();
        assertThat(orderItemProductList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        OrderItemProduct testOrderItemProduct = orderItemProductList.get(orderItemProductList.size() - 1);
        assertThat(testOrderItemProduct.getOrderItemId()).isEqualTo(DEFAULT_ORDER_ITEM_ID);
        assertThat(testOrderItemProduct.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testOrderItemProduct.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void createOrderItemProductWithExistingId() throws Exception {
        // Create the OrderItemProduct with an existing ID
        orderItemProductRepository.saveAndFlush(orderItemProduct);
        OrderItemProductDTO orderItemProductDTO = orderItemProductMapper.toDto(orderItemProduct);

        int databaseSizeBeforeCreate = orderItemProductRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderItemProductMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderItemProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItemProduct in the database
        List<OrderItemProduct> orderItemProductList = orderItemProductRepository.findAll();
        assertThat(orderItemProductList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllOrderItemProducts() throws Exception {
        // Initialize the database
        orderItemProductRepository.saveAndFlush(orderItemProduct);

        // Get all the orderItemProductList
        restOrderItemProductMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderItemProduct.getId().toString())))
            .andExpect(jsonPath("$.[*].orderItemId").value(hasItem(DEFAULT_ORDER_ITEM_ID.toString())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));
    }

    @Test
    @Transactional
    void getOrderItemProduct() throws Exception {
        // Initialize the database
        orderItemProductRepository.saveAndFlush(orderItemProduct);

        // Get the orderItemProduct
        restOrderItemProductMockMvc
            .perform(get(ENTITY_API_URL_ID, orderItemProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderItemProduct.getId().toString()))
            .andExpect(jsonPath("$.orderItemId").value(DEFAULT_ORDER_ITEM_ID.toString()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.toString()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY));
    }

    @Test
    @Transactional
    void getNonExistingOrderItemProduct() throws Exception {
        // Get the orderItemProduct
        restOrderItemProductMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingOrderItemProduct() throws Exception {
        // Initialize the database
        orderItemProductRepository.saveAndFlush(orderItemProduct);

        int databaseSizeBeforeUpdate = orderItemProductRepository.findAll().size();
        orderItemProductSearchRepository.save(orderItemProduct);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());

        // Update the orderItemProduct
        OrderItemProduct updatedOrderItemProduct = orderItemProductRepository.findById(orderItemProduct.getId()).get();
        // Disconnect from session so that the updates on updatedOrderItemProduct are not directly saved in db
        em.detach(updatedOrderItemProduct);
        updatedOrderItemProduct.orderItemId(UPDATED_ORDER_ITEM_ID).productId(UPDATED_PRODUCT_ID).quantity(UPDATED_QUANTITY);
        OrderItemProductDTO orderItemProductDTO = orderItemProductMapper.toDto(updatedOrderItemProduct);

        restOrderItemProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderItemProductDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderItemProductDTO))
            )
            .andExpect(status().isOk());

        // Validate the OrderItemProduct in the database
        List<OrderItemProduct> orderItemProductList = orderItemProductRepository.findAll();
        assertThat(orderItemProductList).hasSize(databaseSizeBeforeUpdate);
        OrderItemProduct testOrderItemProduct = orderItemProductList.get(orderItemProductList.size() - 1);
        assertThat(testOrderItemProduct.getOrderItemId()).isEqualTo(UPDATED_ORDER_ITEM_ID);
        assertThat(testOrderItemProduct.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testOrderItemProduct.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<OrderItemProduct> orderItemProductSearchList = IterableUtils.toList(orderItemProductSearchRepository.findAll());
                OrderItemProduct testOrderItemProductSearch = orderItemProductSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testOrderItemProductSearch.getOrderItemId()).isEqualTo(UPDATED_ORDER_ITEM_ID);
                assertThat(testOrderItemProductSearch.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
                assertThat(testOrderItemProductSearch.getQuantity()).isEqualTo(UPDATED_QUANTITY);
            });
    }

    @Test
    @Transactional
    void putNonExistingOrderItemProduct() throws Exception {
        int databaseSizeBeforeUpdate = orderItemProductRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        orderItemProduct.setId(UUID.randomUUID());

        // Create the OrderItemProduct
        OrderItemProductDTO orderItemProductDTO = orderItemProductMapper.toDto(orderItemProduct);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderItemProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, orderItemProductDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderItemProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItemProduct in the database
        List<OrderItemProduct> orderItemProductList = orderItemProductRepository.findAll();
        assertThat(orderItemProductList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchOrderItemProduct() throws Exception {
        int databaseSizeBeforeUpdate = orderItemProductRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        orderItemProduct.setId(UUID.randomUUID());

        // Create the OrderItemProduct
        OrderItemProductDTO orderItemProductDTO = orderItemProductMapper.toDto(orderItemProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemProductMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderItemProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItemProduct in the database
        List<OrderItemProduct> orderItemProductList = orderItemProductRepository.findAll();
        assertThat(orderItemProductList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOrderItemProduct() throws Exception {
        int databaseSizeBeforeUpdate = orderItemProductRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        orderItemProduct.setId(UUID.randomUUID());

        // Create the OrderItemProduct
        OrderItemProductDTO orderItemProductDTO = orderItemProductMapper.toDto(orderItemProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemProductMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(orderItemProductDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderItemProduct in the database
        List<OrderItemProduct> orderItemProductList = orderItemProductRepository.findAll();
        assertThat(orderItemProductList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateOrderItemProductWithPatch() throws Exception {
        // Initialize the database
        orderItemProductRepository.saveAndFlush(orderItemProduct);

        int databaseSizeBeforeUpdate = orderItemProductRepository.findAll().size();

        // Update the orderItemProduct using partial update
        OrderItemProduct partialUpdatedOrderItemProduct = new OrderItemProduct();
        partialUpdatedOrderItemProduct.setId(orderItemProduct.getId());

        restOrderItemProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderItemProduct.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderItemProduct))
            )
            .andExpect(status().isOk());

        // Validate the OrderItemProduct in the database
        List<OrderItemProduct> orderItemProductList = orderItemProductRepository.findAll();
        assertThat(orderItemProductList).hasSize(databaseSizeBeforeUpdate);
        OrderItemProduct testOrderItemProduct = orderItemProductList.get(orderItemProductList.size() - 1);
        assertThat(testOrderItemProduct.getOrderItemId()).isEqualTo(DEFAULT_ORDER_ITEM_ID);
        assertThat(testOrderItemProduct.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testOrderItemProduct.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void fullUpdateOrderItemProductWithPatch() throws Exception {
        // Initialize the database
        orderItemProductRepository.saveAndFlush(orderItemProduct);

        int databaseSizeBeforeUpdate = orderItemProductRepository.findAll().size();

        // Update the orderItemProduct using partial update
        OrderItemProduct partialUpdatedOrderItemProduct = new OrderItemProduct();
        partialUpdatedOrderItemProduct.setId(orderItemProduct.getId());

        partialUpdatedOrderItemProduct.orderItemId(UPDATED_ORDER_ITEM_ID).productId(UPDATED_PRODUCT_ID).quantity(UPDATED_QUANTITY);

        restOrderItemProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOrderItemProduct.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderItemProduct))
            )
            .andExpect(status().isOk());

        // Validate the OrderItemProduct in the database
        List<OrderItemProduct> orderItemProductList = orderItemProductRepository.findAll();
        assertThat(orderItemProductList).hasSize(databaseSizeBeforeUpdate);
        OrderItemProduct testOrderItemProduct = orderItemProductList.get(orderItemProductList.size() - 1);
        assertThat(testOrderItemProduct.getOrderItemId()).isEqualTo(UPDATED_ORDER_ITEM_ID);
        assertThat(testOrderItemProduct.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testOrderItemProduct.getQuantity()).isEqualTo(UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void patchNonExistingOrderItemProduct() throws Exception {
        int databaseSizeBeforeUpdate = orderItemProductRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        orderItemProduct.setId(UUID.randomUUID());

        // Create the OrderItemProduct
        OrderItemProductDTO orderItemProductDTO = orderItemProductMapper.toDto(orderItemProduct);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderItemProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, orderItemProductDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderItemProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItemProduct in the database
        List<OrderItemProduct> orderItemProductList = orderItemProductRepository.findAll();
        assertThat(orderItemProductList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOrderItemProduct() throws Exception {
        int databaseSizeBeforeUpdate = orderItemProductRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        orderItemProduct.setId(UUID.randomUUID());

        // Create the OrderItemProduct
        OrderItemProductDTO orderItemProductDTO = orderItemProductMapper.toDto(orderItemProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemProductMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderItemProductDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OrderItemProduct in the database
        List<OrderItemProduct> orderItemProductList = orderItemProductRepository.findAll();
        assertThat(orderItemProductList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOrderItemProduct() throws Exception {
        int databaseSizeBeforeUpdate = orderItemProductRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        orderItemProduct.setId(UUID.randomUUID());

        // Create the OrderItemProduct
        OrderItemProductDTO orderItemProductDTO = orderItemProductMapper.toDto(orderItemProduct);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOrderItemProductMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(orderItemProductDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OrderItemProduct in the database
        List<OrderItemProduct> orderItemProductList = orderItemProductRepository.findAll();
        assertThat(orderItemProductList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteOrderItemProduct() throws Exception {
        // Initialize the database
        orderItemProductRepository.saveAndFlush(orderItemProduct);
        orderItemProductRepository.save(orderItemProduct);
        orderItemProductSearchRepository.save(orderItemProduct);

        int databaseSizeBeforeDelete = orderItemProductRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the orderItemProduct
        restOrderItemProductMockMvc
            .perform(delete(ENTITY_API_URL_ID, orderItemProduct.getId().toString()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderItemProduct> orderItemProductList = orderItemProductRepository.findAll();
        assertThat(orderItemProductList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(orderItemProductSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchOrderItemProduct() throws Exception {
        // Initialize the database
        orderItemProduct = orderItemProductRepository.saveAndFlush(orderItemProduct);
        orderItemProductSearchRepository.save(orderItemProduct);

        // Search the orderItemProduct
        restOrderItemProductMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + orderItemProduct.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderItemProduct.getId().toString())))
            .andExpect(jsonPath("$.[*].orderItemId").value(hasItem(DEFAULT_ORDER_ITEM_ID.toString())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));
    }
}
