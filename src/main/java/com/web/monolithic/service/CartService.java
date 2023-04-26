package com.web.monolithic.service;

import com.web.monolithic.service.dto.CartDTO;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.web.monolithic.domain.Cart}.
 */
public interface CartService {
    /**
     * Save a cart.
     *
     * @param cartDTO the entity to save.
     * @return the persisted entity.
     */
    CartDTO save(CartDTO cartDTO);

    /**
     * Updates a cart.
     *
     * @param cartDTO the entity to update.
     * @return the persisted entity.
     */
    CartDTO update(CartDTO cartDTO);

    /**
     * Partially updates a cart.
     *
     * @param cartDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CartDTO> partialUpdate(CartDTO cartDTO);

    /**
     * Get all the carts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CartDTO> findAll(Pageable pageable);

    /**
     * Get the "id" cart.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CartDTO> findOne(UUID id);

    /**
     * Delete the "id" cart.
     *
     * @param id the id of the entity.
     */
    void delete(UUID id);

    /**
     * Search for the cart corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CartDTO> search(String query, Pageable pageable);
}
