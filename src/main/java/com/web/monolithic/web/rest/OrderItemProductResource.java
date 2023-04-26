package com.web.monolithic.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.web.monolithic.repository.OrderItemProductRepository;
import com.web.monolithic.service.OrderItemProductService;
import com.web.monolithic.service.dto.OrderItemProductDTO;
import com.web.monolithic.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.web.monolithic.domain.OrderItemProduct}.
 */
@RestController
@RequestMapping("/api")
public class OrderItemProductResource {

    private final Logger log = LoggerFactory.getLogger(OrderItemProductResource.class);

    private static final String ENTITY_NAME = "orderItemProduct";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderItemProductService orderItemProductService;

    private final OrderItemProductRepository orderItemProductRepository;

    public OrderItemProductResource(
        OrderItemProductService orderItemProductService,
        OrderItemProductRepository orderItemProductRepository
    ) {
        this.orderItemProductService = orderItemProductService;
        this.orderItemProductRepository = orderItemProductRepository;
    }

    /**
     * {@code POST  /order-item-products} : Create a new orderItemProduct.
     *
     * @param orderItemProductDTO the orderItemProductDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderItemProductDTO, or with status {@code 400 (Bad Request)} if the orderItemProduct has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/order-item-products")
    public ResponseEntity<OrderItemProductDTO> createOrderItemProduct(@RequestBody OrderItemProductDTO orderItemProductDTO)
        throws URISyntaxException {
        log.debug("REST request to save OrderItemProduct : {}", orderItemProductDTO);
        if (orderItemProductDTO.getId() != null) {
            throw new BadRequestAlertException("A new orderItemProduct cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OrderItemProductDTO result = orderItemProductService.save(orderItemProductDTO);
        return ResponseEntity
            .created(new URI("/api/order-item-products/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /order-item-products/:id} : Updates an existing orderItemProduct.
     *
     * @param id the id of the orderItemProductDTO to save.
     * @param orderItemProductDTO the orderItemProductDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItemProductDTO,
     * or with status {@code 400 (Bad Request)} if the orderItemProductDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderItemProductDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/order-item-products/{id}")
    public ResponseEntity<OrderItemProductDTO> updateOrderItemProduct(
        @PathVariable(value = "id", required = false) final UUID id,
        @RequestBody OrderItemProductDTO orderItemProductDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OrderItemProduct : {}, {}", id, orderItemProductDTO);
        if (orderItemProductDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItemProductDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderItemProductRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OrderItemProductDTO result = orderItemProductService.update(orderItemProductDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderItemProductDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /order-item-products/:id} : Partial updates given fields of an existing orderItemProduct, field will ignore if it is null
     *
     * @param id the id of the orderItemProductDTO to save.
     * @param orderItemProductDTO the orderItemProductDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderItemProductDTO,
     * or with status {@code 400 (Bad Request)} if the orderItemProductDTO is not valid,
     * or with status {@code 404 (Not Found)} if the orderItemProductDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderItemProductDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/order-item-products/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<OrderItemProductDTO> partialUpdateOrderItemProduct(
        @PathVariable(value = "id", required = false) final UUID id,
        @RequestBody OrderItemProductDTO orderItemProductDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrderItemProduct partially : {}, {}", id, orderItemProductDTO);
        if (orderItemProductDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderItemProductDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!orderItemProductRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OrderItemProductDTO> result = orderItemProductService.partialUpdate(orderItemProductDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, orderItemProductDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /order-item-products} : get all the orderItemProducts.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderItemProducts in body.
     */
    @GetMapping("/order-item-products")
    public ResponseEntity<List<OrderItemProductDTO>> getAllOrderItemProducts(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of OrderItemProducts");
        Page<OrderItemProductDTO> page = orderItemProductService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /order-item-products/:id} : get the "id" orderItemProduct.
     *
     * @param id the id of the orderItemProductDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderItemProductDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/order-item-products/{id}")
    public ResponseEntity<OrderItemProductDTO> getOrderItemProduct(@PathVariable UUID id) {
        log.debug("REST request to get OrderItemProduct : {}", id);
        Optional<OrderItemProductDTO> orderItemProductDTO = orderItemProductService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderItemProductDTO);
    }

    /**
     * {@code DELETE  /order-item-products/:id} : delete the "id" orderItemProduct.
     *
     * @param id the id of the orderItemProductDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/order-item-products/{id}")
    public ResponseEntity<Void> deleteOrderItemProduct(@PathVariable UUID id) {
        log.debug("REST request to delete OrderItemProduct : {}", id);
        orderItemProductService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/order-item-products?query=:query} : search for the orderItemProduct corresponding
     * to the query.
     *
     * @param query the query of the orderItemProduct search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/order-item-products")
    public ResponseEntity<List<OrderItemProductDTO>> searchOrderItemProducts(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of OrderItemProducts for query {}", query);
        Page<OrderItemProductDTO> page = orderItemProductService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
