package com.web.monolithic.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.web.monolithic.IntegrationTest;
import com.web.monolithic.domain.CartItem;
import com.web.monolithic.repository.CartItemRepository;
import com.web.monolithic.repository.search.CartItemSearchRepository;
import com.web.monolithic.service.dto.CartItemDTO;
import com.web.monolithic.service.mapper.CartItemMapper;
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
 * Integration tests for the {@link CartItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CartItemResourceIT {

    private static final UUID DEFAULT_CART_ID = UUID.randomUUID();
    private static final UUID UPDATED_CART_ID = UUID.randomUUID();

    private static final UUID DEFAULT_PRODUCT_ID = UUID.randomUUID();
    private static final UUID UPDATED_PRODUCT_ID = UUID.randomUUID();

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final String ENTITY_API_URL = "/api/cart-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/cart-items";

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private CartItemSearchRepository cartItemSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCartItemMockMvc;

    private CartItem cartItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartItem createEntity(EntityManager em) {
        CartItem cartItem = new CartItem().cartId(DEFAULT_CART_ID).productId(DEFAULT_PRODUCT_ID).quantity(DEFAULT_QUANTITY);
        return cartItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartItem createUpdatedEntity(EntityManager em) {
        CartItem cartItem = new CartItem().cartId(UPDATED_CART_ID).productId(UPDATED_PRODUCT_ID).quantity(UPDATED_QUANTITY);
        return cartItem;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        cartItemSearchRepository.deleteAll();
        assertThat(cartItemSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        cartItem = createEntity(em);
    }

    @Test
    @Transactional
    void createCartItem() throws Exception {
        int databaseSizeBeforeCreate = cartItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);
        restCartItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartItemDTO))
            )
            .andExpect(status().isCreated());

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        CartItem testCartItem = cartItemList.get(cartItemList.size() - 1);
        assertThat(testCartItem.getCartId()).isEqualTo(DEFAULT_CART_ID);
        assertThat(testCartItem.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testCartItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    void createCartItemWithExistingId() throws Exception {
        // Create the CartItem with an existing ID
        cartItemRepository.saveAndFlush(cartItem);
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        int databaseSizeBeforeCreate = cartItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCartItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCartItems() throws Exception {
        // Initialize the database
        cartItemRepository.saveAndFlush(cartItem);

        // Get all the cartItemList
        restCartItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cartItem.getId().toString())))
            .andExpect(jsonPath("$.[*].cartId").value(hasItem(DEFAULT_CART_ID.toString())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));
    }

    @Test
    @Transactional
    void getCartItem() throws Exception {
        // Initialize the database
        cartItemRepository.saveAndFlush(cartItem);

        // Get the cartItem
        restCartItemMockMvc
            .perform(get(ENTITY_API_URL_ID, cartItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cartItem.getId().toString()))
            .andExpect(jsonPath("$.cartId").value(DEFAULT_CART_ID.toString()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID.toString()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY));
    }

    @Test
    @Transactional
    void getNonExistingCartItem() throws Exception {
        // Get the cartItem
        restCartItemMockMvc.perform(get(ENTITY_API_URL_ID, UUID.randomUUID().toString())).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCartItem() throws Exception {
        // Initialize the database
        cartItemRepository.saveAndFlush(cartItem);

        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();
        cartItemSearchRepository.save(cartItem);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());

        // Update the cartItem
        CartItem updatedCartItem = cartItemRepository.findById(cartItem.getId()).get();
        // Disconnect from session so that the updates on updatedCartItem are not directly saved in db
        em.detach(updatedCartItem);
        updatedCartItem.cartId(UPDATED_CART_ID).productId(UPDATED_PRODUCT_ID).quantity(UPDATED_QUANTITY);
        CartItemDTO cartItemDTO = cartItemMapper.toDto(updatedCartItem);

        restCartItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cartItemDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        CartItem testCartItem = cartItemList.get(cartItemList.size() - 1);
        assertThat(testCartItem.getCartId()).isEqualTo(UPDATED_CART_ID);
        assertThat(testCartItem.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testCartItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CartItem> cartItemSearchList = IterableUtils.toList(cartItemSearchRepository.findAll());
                CartItem testCartItemSearch = cartItemSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCartItemSearch.getCartId()).isEqualTo(UPDATED_CART_ID);
                assertThat(testCartItemSearch.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
                assertThat(testCartItemSearch.getQuantity()).isEqualTo(UPDATED_QUANTITY);
            });
    }

    @Test
    @Transactional
    void putNonExistingCartItem() throws Exception {
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        cartItem.setId(UUID.randomUUID());

        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cartItemDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCartItem() throws Exception {
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        cartItem.setId(UUID.randomUUID());

        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCartItem() throws Exception {
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        cartItem.setId(UUID.randomUUID());

        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(cartItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCartItemWithPatch() throws Exception {
        // Initialize the database
        cartItemRepository.saveAndFlush(cartItem);

        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();

        // Update the cartItem using partial update
        CartItem partialUpdatedCartItem = new CartItem();
        partialUpdatedCartItem.setId(cartItem.getId());

        partialUpdatedCartItem.productId(UPDATED_PRODUCT_ID).quantity(UPDATED_QUANTITY);

        restCartItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCartItem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCartItem))
            )
            .andExpect(status().isOk());

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        CartItem testCartItem = cartItemList.get(cartItemList.size() - 1);
        assertThat(testCartItem.getCartId()).isEqualTo(DEFAULT_CART_ID);
        assertThat(testCartItem.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testCartItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void fullUpdateCartItemWithPatch() throws Exception {
        // Initialize the database
        cartItemRepository.saveAndFlush(cartItem);

        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();

        // Update the cartItem using partial update
        CartItem partialUpdatedCartItem = new CartItem();
        partialUpdatedCartItem.setId(cartItem.getId());

        partialUpdatedCartItem.cartId(UPDATED_CART_ID).productId(UPDATED_PRODUCT_ID).quantity(UPDATED_QUANTITY);

        restCartItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCartItem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCartItem))
            )
            .andExpect(status().isOk());

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        CartItem testCartItem = cartItemList.get(cartItemList.size() - 1);
        assertThat(testCartItem.getCartId()).isEqualTo(UPDATED_CART_ID);
        assertThat(testCartItem.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testCartItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    void patchNonExistingCartItem() throws Exception {
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        cartItem.setId(UUID.randomUUID());

        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cartItemDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cartItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCartItem() throws Exception {
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        cartItem.setId(UUID.randomUUID());

        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cartItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCartItem() throws Exception {
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        cartItem.setId(UUID.randomUUID());

        // Create the CartItem
        CartItemDTO cartItemDTO = cartItemMapper.toDto(cartItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCartItemMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(cartItemDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCartItem() throws Exception {
        // Initialize the database
        cartItemRepository.saveAndFlush(cartItem);
        cartItemRepository.save(cartItem);
        cartItemSearchRepository.save(cartItem);

        int databaseSizeBeforeDelete = cartItemRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the cartItem
        restCartItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, cartItem.getId().toString()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CartItem> cartItemList = cartItemRepository.findAll();
        assertThat(cartItemList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cartItemSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCartItem() throws Exception {
        // Initialize the database
        cartItem = cartItemRepository.saveAndFlush(cartItem);
        cartItemSearchRepository.save(cartItem);

        // Search the cartItem
        restCartItemMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + cartItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cartItem.getId().toString())))
            .andExpect(jsonPath("$.[*].cartId").value(hasItem(DEFAULT_CART_ID.toString())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID.toString())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));
    }
}
