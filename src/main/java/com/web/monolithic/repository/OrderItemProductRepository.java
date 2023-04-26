package com.web.monolithic.repository;

import com.web.monolithic.domain.OrderItemProduct;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrderItemProduct entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderItemProductRepository extends JpaRepository<OrderItemProduct, UUID> {}
