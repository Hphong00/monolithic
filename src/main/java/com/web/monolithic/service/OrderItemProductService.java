package com.web.monolithic.service;

import com.web.monolithic.service.dto.OrderItemProductDTO;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.web.monolithic.domain.OrderItemProduct}.
 */
public interface OrderItemProductService {
    /**
     * Save a orderItemProduct.
     *
     * @param orderItemProductDTO the entity to save.
     * @return the persisted entity.
     */
    OrderItemProductDTO save(OrderItemProductDTO orderItemProductDTO);

    /**
     * Updates a orderItemProduct.
     *
     * @param orderItemProductDTO the entity to update.
     * @return the persisted entity.
     */
    OrderItemProductDTO update(OrderItemProductDTO orderItemProductDTO);

    /**
     * Partially updates a orderItemProduct.
     *
     * @param orderItemProductDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<OrderItemProductDTO> partialUpdate(OrderItemProductDTO orderItemProductDTO);

    /**
     * Get all the orderItemProducts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OrderItemProductDTO> findAll(Pageable pageable);

    /**
     * Get the "id" orderItemProduct.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OrderItemProductDTO> findOne(UUID id);

    /**
     * Delete the "id" orderItemProduct.
     *
     * @param id the id of the entity.
     */
    void delete(UUID id);

    /**
     * Search for the orderItemProduct corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OrderItemProductDTO> search(String query, Pageable pageable);
}
