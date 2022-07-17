package com.erestaurant.erestaurantapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Order.
 */
@Table("jhi_order")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "order")
public class Order implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "must not be null")
    @Size(min = 5)
    @Id
    @Column("id")
    private String id;

    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("rating")
    private Float rating;

    @Transient
    private boolean isPersisted;

    @Transient
    private OrderType type;

    @Transient
    private State state;

    @Transient
    @JsonIgnoreProperties(value = { "profile", "orders" }, allowSetters = true)
    private Customer customer;

    @Column("type_id")
    private String typeId;

    @Column("state_id")
    private String stateId;

    @Column("customer_id")
    private String customerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Order id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Order description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getRating() {
        return this.rating;
    }

    public Order rating(Float rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Order setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public OrderType getType() {
        return this.type;
    }

    public void setType(OrderType orderType) {
        this.type = orderType;
        this.typeId = orderType != null ? orderType.getId() : null;
    }

    public Order type(OrderType orderType) {
        this.setType(orderType);
        return this;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
        this.stateId = state != null ? state.getId() : null;
    }

    public Order state(State state) {
        this.setState(state);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.customerId = customer != null ? customer.getId() : null;
    }

    public Order customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public String getTypeId() {
        return this.typeId;
    }

    public void setTypeId(String orderType) {
        this.typeId = orderType;
    }

    public String getStateId() {
        return this.stateId;
    }

    public void setStateId(String state) {
        this.stateId = state;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(String customer) {
        this.customerId = customer;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        return id != null && id.equals(((Order) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Order{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", rating=" + getRating() +
            "}";
    }
}
