package com.web.monolithic.domain;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A OrderItemProduct.
 */
@Entity
@Table(name = "order_item_product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "orderitemproduct")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrderItemProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "order_item_id")
    private UUID orderItemId;

    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "quantity")
    private Integer quantity;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public OrderItemProduct id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderItemId() {
        return this.orderItemId;
    }

    public OrderItemProduct orderItemId(UUID orderItemId) {
        this.setOrderItemId(orderItemId);
        return this;
    }

    public void setOrderItemId(UUID orderItemId) {
        this.orderItemId = orderItemId;
    }

    public UUID getProductId() {
        return this.productId;
    }

    public OrderItemProduct productId(UUID productId) {
        this.setProductId(productId);
        return this;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public OrderItemProduct quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderItemProduct)) {
            return false;
        }
        return id != null && id.equals(((OrderItemProduct) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderItemProduct{" +
            "id=" + getId() +
            ", orderItemId='" + getOrderItemId() + "'" +
            ", productId='" + getProductId() + "'" +
            ", quantity=" + getQuantity() +
            "}";
    }
}
