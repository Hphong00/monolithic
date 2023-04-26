package com.web.monolithic.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Payment.
 */
@Entity
@Table(name = "payment")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "payment")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "card_holder_name")
    private String cardHolderName;

    @Column(name = "card_expiration_month")
    private Integer cardExpirationMonth;

    @Column(name = "card_expiration_year")
    private Integer cardExpirationYear;

    @Column(name = "card_cvv")
    private String cardCVV;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public UUID getId() {
        return this.id;
    }

    public Payment id(UUID id) {
        this.setId(id);
        return this;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return this.userId;
    }

    public Payment userId(UUID userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public Payment cardNumber(String cardNumber) {
        this.setCardNumber(cardNumber);
        return this;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolderName() {
        return this.cardHolderName;
    }

    public Payment cardHolderName(String cardHolderName) {
        this.setCardHolderName(cardHolderName);
        return this;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public Integer getCardExpirationMonth() {
        return this.cardExpirationMonth;
    }

    public Payment cardExpirationMonth(Integer cardExpirationMonth) {
        this.setCardExpirationMonth(cardExpirationMonth);
        return this;
    }

    public void setCardExpirationMonth(Integer cardExpirationMonth) {
        this.cardExpirationMonth = cardExpirationMonth;
    }

    public Integer getCardExpirationYear() {
        return this.cardExpirationYear;
    }

    public Payment cardExpirationYear(Integer cardExpirationYear) {
        this.setCardExpirationYear(cardExpirationYear);
        return this;
    }

    public void setCardExpirationYear(Integer cardExpirationYear) {
        this.cardExpirationYear = cardExpirationYear;
    }

    public String getCardCVV() {
        return this.cardCVV;
    }

    public Payment cardCVV(String cardCVV) {
        this.setCardCVV(cardCVV);
        return this;
    }

    public void setCardCVV(String cardCVV) {
        this.cardCVV = cardCVV;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Payment createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Payment updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Payment)) {
            return false;
        }
        return id != null && id.equals(((Payment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Payment{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", cardNumber='" + getCardNumber() + "'" +
            ", cardHolderName='" + getCardHolderName() + "'" +
            ", cardExpirationMonth=" + getCardExpirationMonth() +
            ", cardExpirationYear=" + getCardExpirationYear() +
            ", cardCVV='" + getCardCVV() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
