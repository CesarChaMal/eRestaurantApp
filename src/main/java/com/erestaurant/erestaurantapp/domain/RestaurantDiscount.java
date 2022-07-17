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
 * A RestaurantDiscount.
 */
@Table("restaurant_discount")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "restaurantdiscount")
public class RestaurantDiscount implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "must not be null")
    @Size(min = 5)
    @Id
    @Column("id")
    private String id;

    @NotNull(message = "must not be null")
    @Size(min = 3)
    @Column("code")
    private String code;

    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @Column("percentage")
    private Float percentage;

    @Transient
    private boolean isPersisted;

    @Transient
    @JsonIgnoreProperties(value = { "category", "discounts", "restaurant", "cart" }, allowSetters = true)
    private Products products;

    @Column("products_id")
    private String productsId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public RestaurantDiscount id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public RestaurantDiscount code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public RestaurantDiscount description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPercentage() {
        return this.percentage;
    }

    public RestaurantDiscount percentage(Float percentage) {
        this.setPercentage(percentage);
        return this;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public RestaurantDiscount setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Products getProducts() {
        return this.products;
    }

    public void setProducts(Products products) {
        this.products = products;
        this.productsId = products != null ? products.getId() : null;
    }

    public RestaurantDiscount products(Products products) {
        this.setProducts(products);
        return this;
    }

    public String getProductsId() {
        return this.productsId;
    }

    public void setProductsId(String products) {
        this.productsId = products;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestaurantDiscount)) {
            return false;
        }
        return id != null && id.equals(((RestaurantDiscount) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurantDiscount{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", description='" + getDescription() + "'" +
            ", percentage=" + getPercentage() +
            "}";
    }
}
