package com.erestaurant.erestaurantapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Cart.
 */
@Table("cart")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "cart")
public class Cart implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "must not be null")
    @Size(min = 5)
    @Id
    @Column("id")
    private String id;

    @Column("description")
    private String description;

    @Transient
    private boolean isPersisted;

    @Transient
    private Order order;

    @Transient
    @JsonIgnoreProperties(value = { "category", "discounts", "restaurant", "cart" }, allowSetters = true)
    private Set<Products> products = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "cart" }, allowSetters = true)
    private Set<AppDiscount> discounts = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "cart" }, allowSetters = true)
    private Set<Payment> payments = new HashSet<>();

    @Column("order_id")
    private String orderId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Cart id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Cart description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Cart setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
        this.orderId = order != null ? order.getId() : null;
    }

    public Cart order(Order order) {
        this.setOrder(order);
        return this;
    }

    public Set<Products> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Products> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.setCart(null));
        }
        if (products != null) {
            products.forEach(i -> i.setCart(this));
        }
        this.products = products;
    }

    public Cart products(Set<Products> products) {
        this.setProducts(products);
        return this;
    }

    public Cart addProducts(Products products) {
        this.products.add(products);
        products.setCart(this);
        return this;
    }

    public Cart removeProducts(Products products) {
        this.products.remove(products);
        products.setCart(null);
        return this;
    }

    public Set<AppDiscount> getDiscounts() {
        return this.discounts;
    }

    public void setDiscounts(Set<AppDiscount> appDiscounts) {
        if (this.discounts != null) {
            this.discounts.forEach(i -> i.setCart(null));
        }
        if (appDiscounts != null) {
            appDiscounts.forEach(i -> i.setCart(this));
        }
        this.discounts = appDiscounts;
    }

    public Cart discounts(Set<AppDiscount> appDiscounts) {
        this.setDiscounts(appDiscounts);
        return this;
    }

    public Cart addDiscounts(AppDiscount appDiscount) {
        this.discounts.add(appDiscount);
        appDiscount.setCart(this);
        return this;
    }

    public Cart removeDiscounts(AppDiscount appDiscount) {
        this.discounts.remove(appDiscount);
        appDiscount.setCart(null);
        return this;
    }

    public Set<Payment> getPayments() {
        return this.payments;
    }

    public void setPayments(Set<Payment> payments) {
        if (this.payments != null) {
            this.payments.forEach(i -> i.setCart(null));
        }
        if (payments != null) {
            payments.forEach(i -> i.setCart(this));
        }
        this.payments = payments;
    }

    public Cart payments(Set<Payment> payments) {
        this.setPayments(payments);
        return this;
    }

    public Cart addPayments(Payment payment) {
        this.payments.add(payment);
        payment.setCart(this);
        return this;
    }

    public Cart removePayments(Payment payment) {
        this.payments.remove(payment);
        payment.setCart(null);
        return this;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void setOrderId(String order) {
        this.orderId = order;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cart)) {
            return false;
        }
        return id != null && id.equals(((Cart) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cart{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
