package com.web.monolithic.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.web.monolithic.repository.CartItemRepository;
import com.web.monolithic.service.CartItemService;
import com.web.monolithic.service.dto.CartItemDTO;
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
 * REST controller for managing {@link com.web.monolithic.domain.CartItem}.
 */
@RestController
@RequestMapping("/api")
public class CartItemResource {

    private final Logger log = LoggerFactory.getLogger(CartItemResource.class);

    private static final String ENTITY_NAME = "cartItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartItemService cartItemService;

    private final CartItemRepository cartItemRepository;

    public CartItemResource(CartItemService cartItemService, CartItemRepository cartItemRepository) {
        this.cartItemService = cartItemService;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * {@code POST  /cart-items} : Create a new cartItem.
     *
     * @param cartItemDTO the cartItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cartItemDTO, or with status {@code 400 (Bad Request)} if the cartItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cart-items")
    public ResponseEntity<CartItemDTO> createCartItem(@RequestBody CartItemDTO cartItemDTO) throws URISyntaxException {
        log.debug("REST request to save CartItem : {}", cartItemDTO);
        if (cartItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new cartItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CartItemDTO result = cartItemService.save(cartItemDTO);
        return ResponseEntity
            .created(new URI("/api/cart-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cart-items/:id} : Updates an existing cartItem.
     *
     * @param id the id of the cartItemDTO to save.
     * @param cartItemDTO the cartItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartItemDTO,
     * or with status {@code 400 (Bad Request)} if the cartItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cartItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cart-items/{id}")
    public ResponseEntity<CartItemDTO> updateCartItem(
        @PathVariable(value = "id", required = false) final UUID id,
        @RequestBody CartItemDTO cartItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to update CartItem : {}, {}", id, cartItemDTO);
        if (cartItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cartItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CartItemDTO result = cartItemService.update(cartItemDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cartItemDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /cart-items/:id} : Partial updates given fields of an existing cartItem, field will ignore if it is null
     *
     * @param id the id of the cartItemDTO to save.
     * @param cartItemDTO the cartItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cartItemDTO,
     * or with status {@code 400 (Bad Request)} if the cartItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cartItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cartItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/cart-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CartItemDTO> partialUpdateCartItem(
        @PathVariable(value = "id", required = false) final UUID id,
        @RequestBody CartItemDTO cartItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update CartItem partially : {}, {}", id, cartItemDTO);
        if (cartItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cartItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CartItemDTO> result = cartItemService.partialUpdate(cartItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cartItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cart-items} : get all the cartItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cartItems in body.
     */
    @GetMapping("/cart-items")
    public ResponseEntity<List<CartItemDTO>> getAllCartItems(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of CartItems");
        Page<CartItemDTO> page = cartItemService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cart-items/:id} : get the "id" cartItem.
     *
     * @param id the id of the cartItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cartItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cart-items/{id}")
    public ResponseEntity<CartItemDTO> getCartItem(@PathVariable UUID id) {
        log.debug("REST request to get CartItem : {}", id);
        Optional<CartItemDTO> cartItemDTO = cartItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cartItemDTO);
    }

    /**
     * {@code DELETE  /cart-items/:id} : delete the "id" cartItem.
     *
     * @param id the id of the cartItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cart-items/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable UUID id) {
        log.debug("REST request to delete CartItem : {}", id);
        cartItemService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/cart-items?query=:query} : search for the cartItem corresponding
     * to the query.
     *
     * @param query the query of the cartItem search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/cart-items")
    public ResponseEntity<List<CartItemDTO>> searchCartItems(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of CartItems for query {}", query);
        Page<CartItemDTO> page = cartItemService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
