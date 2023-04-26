package com.web.monolithic.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.web.monolithic.repository.ShippingRepository;
import com.web.monolithic.service.ShippingService;
import com.web.monolithic.service.dto.ShippingDTO;
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
 * REST controller for managing {@link com.web.monolithic.domain.Shipping}.
 */
@RestController
@RequestMapping("/api")
public class ShippingResource {

    private final Logger log = LoggerFactory.getLogger(ShippingResource.class);

    private static final String ENTITY_NAME = "shipping";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ShippingService shippingService;

    private final ShippingRepository shippingRepository;

    public ShippingResource(ShippingService shippingService, ShippingRepository shippingRepository) {
        this.shippingService = shippingService;
        this.shippingRepository = shippingRepository;
    }

    /**
     * {@code POST  /shippings} : Create a new shipping.
     *
     * @param shippingDTO the shippingDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new shippingDTO, or with status {@code 400 (Bad Request)} if the shipping has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/shippings")
    public ResponseEntity<ShippingDTO> createShipping(@RequestBody ShippingDTO shippingDTO) throws URISyntaxException {
        log.debug("REST request to save Shipping : {}", shippingDTO);
        if (shippingDTO.getId() != null) {
            throw new BadRequestAlertException("A new shipping cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ShippingDTO result = shippingService.save(shippingDTO);
        return ResponseEntity
            .created(new URI("/api/shippings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /shippings/:id} : Updates an existing shipping.
     *
     * @param id the id of the shippingDTO to save.
     * @param shippingDTO the shippingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shippingDTO,
     * or with status {@code 400 (Bad Request)} if the shippingDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the shippingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/shippings/{id}")
    public ResponseEntity<ShippingDTO> updateShipping(
        @PathVariable(value = "id", required = false) final UUID id,
        @RequestBody ShippingDTO shippingDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Shipping : {}, {}", id, shippingDTO);
        if (shippingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shippingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shippingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ShippingDTO result = shippingService.update(shippingDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shippingDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /shippings/:id} : Partial updates given fields of an existing shipping, field will ignore if it is null
     *
     * @param id the id of the shippingDTO to save.
     * @param shippingDTO the shippingDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated shippingDTO,
     * or with status {@code 400 (Bad Request)} if the shippingDTO is not valid,
     * or with status {@code 404 (Not Found)} if the shippingDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the shippingDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/shippings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ShippingDTO> partialUpdateShipping(
        @PathVariable(value = "id", required = false) final UUID id,
        @RequestBody ShippingDTO shippingDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Shipping partially : {}, {}", id, shippingDTO);
        if (shippingDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, shippingDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!shippingRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ShippingDTO> result = shippingService.partialUpdate(shippingDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, shippingDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /shippings} : get all the shippings.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of shippings in body.
     */
    @GetMapping("/shippings")
    public ResponseEntity<List<ShippingDTO>> getAllShippings(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Shippings");
        Page<ShippingDTO> page = shippingService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /shippings/:id} : get the "id" shipping.
     *
     * @param id the id of the shippingDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the shippingDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/shippings/{id}")
    public ResponseEntity<ShippingDTO> getShipping(@PathVariable UUID id) {
        log.debug("REST request to get Shipping : {}", id);
        Optional<ShippingDTO> shippingDTO = shippingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(shippingDTO);
    }

    /**
     * {@code DELETE  /shippings/:id} : delete the "id" shipping.
     *
     * @param id the id of the shippingDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/shippings/{id}")
    public ResponseEntity<Void> deleteShipping(@PathVariable UUID id) {
        log.debug("REST request to delete Shipping : {}", id);
        shippingService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/shippings?query=:query} : search for the shipping corresponding
     * to the query.
     *
     * @param query the query of the shipping search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/shippings")
    public ResponseEntity<List<ShippingDTO>> searchShippings(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Shippings for query {}", query);
        Page<ShippingDTO> page = shippingService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
