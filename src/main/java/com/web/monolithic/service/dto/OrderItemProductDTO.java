package com.web.monolithic.service.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.web.monolithic.domain.OrderItemProduct} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderItemProductDTO implements Serializable {

    private UUID id;

    private UUID orderItemId;

    private UUID productId;

    private Integer quantity;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(UUID orderItemId) {
        this.orderItemId = orderItemId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderItemProductDTO)) {
            return false;
        }

        OrderItemProductDTO orderItemProductDTO = (OrderItemProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, orderItemProductDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItemProductDTO{" +
            "id='" + getId() + "'" +
            ", orderItemId='" + getOrderItemId() + "'" +
            ", productId='" + getProductId() + "'" +
            ", quantity=" + getQuantity() +
            "}";
    }
}
