package com.web.monolithic.service;

import com.web.monolithic.service.dto.CartItemDTO;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.web.monolithic.domain.CartItem}.
 */
public interface CartItemService {
    /**
     * Save a cartItem.
     *
     * @param cartItemDTO the entity to save.
     * @return the persisted entity.
     */
    CartItemDTO save(CartItemDTO cartItemDTO);

    /**
     * Updates a cartItem.
     *
     * @param cartItemDTO the entity to update.
     * @return the persisted entity.
     */
    CartItemDTO update(CartItemDTO cartItemDTO);

    /**
     * Partially updates a cartItem.
     *
     * @param cartItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CartItemDTO> partialUpdate(CartItemDTO cartItemDTO);

    /**
     * Get all the cartItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CartItemDTO> findAll(Pageable pageable);

    /**
     * Get the "id" cartItem.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CartItemDTO> findOne(UUID id);

    /**
     * Delete the "id" cartItem.
     *
     * @param id the id of the entity.
     */
    void delete(UUID id);

    /**
     * Search for the cartItem corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CartItemDTO> search(String query, Pageable pageable);
}
