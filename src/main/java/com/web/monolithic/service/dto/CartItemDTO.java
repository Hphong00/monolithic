package com.web.monolithic.service.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A DTO for the {@link com.web.monolithic.domain.CartItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CartItemDTO implements Serializable {

    private UUID id;

    private UUID cartId;

    private UUID productId;

    private Integer quantity;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCartId() {
        return cartId;
    }

    public void setCartId(UUID cartId) {
        this.cartId = cartId;
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
        if (!(o instanceof CartItemDTO)) {
            return false;
        }

        CartItemDTO cartItemDTO = (CartItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cartItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CartItemDTO{" +
            "id='" + getId() + "'" +
            ", cartId='" + getCartId() + "'" +
            ", productId='" + getProductId() + "'" +
            ", quantity=" + getQuantity() +
            "}";
    }
}
