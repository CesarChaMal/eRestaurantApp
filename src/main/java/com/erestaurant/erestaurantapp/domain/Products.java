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
 * A Products.
 */
@Table("products")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "products")
public class Products implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "must not be null")
    @Size(min = 5)
    @Id
    @Column("id")
    private String id;

    @NotNull(message = "must not be null")
    @Size(min = 3)
    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("image")
    private byte[] image;

    @Column("image_content_type")
    private String imageContentType;

    @NotNull(message = "must not be null")
    @Column("estimated_preparaing_time")
    private Float estimatedPreparaingTime;

    @Transient
    private boolean isPersisted;

    @Transient
    private Categories category;

    @Transient
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private Set<RestaurantDiscount> discounts = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "products", "ads", "employees", "notifications" }, allowSetters = true)
    private Restaurant restaurant;

    @Transient
    @JsonIgnoreProperties(value = { "order", "products", "discounts", "payments" }, allowSetters = true)
    private Cart cart;

    @Column("category_id")
    private String categoryId;

    @Column("restaurant_id")
    private String restaurantId;

    @Column("cart_id")
    private String cartId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Products id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Products name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Products description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return this.image;
    }

    public Products image(byte[] image) {
        this.setImage(image);
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public Products imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Float getEstimatedPreparaingTime() {
        return this.estimatedPreparaingTime;
    }

    public Products estimatedPreparaingTime(Float estimatedPreparaingTime) {
        this.setEstimatedPreparaingTime(estimatedPreparaingTime);
        return this;
    }

    public void setEstimatedPreparaingTime(Float estimatedPreparaingTime) {
        this.estimatedPreparaingTime = estimatedPreparaingTime;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Products setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Categories getCategory() {
        return this.category;
    }

    public void setCategory(Categories categories) {
        this.category = categories;
        this.categoryId = categories != null ? categories.getId() : null;
    }

    public Products category(Categories categories) {
        this.setCategory(categories);
        return this;
    }

    public Set<RestaurantDiscount> getDiscounts() {
        return this.discounts;
    }

    public void setDiscounts(Set<RestaurantDiscount> restaurantDiscounts) {
        if (this.discounts != null) {
            this.discounts.forEach(i -> i.setProducts(null));
        }
        if (restaurantDiscounts != null) {
            restaurantDiscounts.forEach(i -> i.setProducts(this));
        }
        this.discounts = restaurantDiscounts;
    }

    public Products discounts(Set<RestaurantDiscount> restaurantDiscounts) {
        this.setDiscounts(restaurantDiscounts);
        return this;
    }

    public Products addDiscounts(RestaurantDiscount restaurantDiscount) {
        this.discounts.add(restaurantDiscount);
        restaurantDiscount.setProducts(this);
        return this;
    }

    public Products removeDiscounts(RestaurantDiscount restaurantDiscount) {
        this.discounts.remove(restaurantDiscount);
        restaurantDiscount.setProducts(null);
        return this;
    }

    public Restaurant getRestaurant() {
        return this.restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        this.restaurantId = restaurant != null ? restaurant.getId() : null;
    }

    public Products restaurant(Restaurant restaurant) {
        this.setRestaurant(restaurant);
        return this;
    }

    public Cart getCart() {
        return this.cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
        this.cartId = cart != null ? cart.getId() : null;
    }

    public Products cart(Cart cart) {
        this.setCart(cart);
        return this;
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String categories) {
        this.categoryId = categories;
    }

    public String getRestaurantId() {
        return this.restaurantId;
    }

    public void setRestaurantId(String restaurant) {
        this.restaurantId = restaurant;
    }

    public String getCartId() {
        return this.cartId;
    }

    public void setCartId(String cart) {
        this.cartId = cart;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Products)) {
            return false;
        }
        return id != null && id.equals(((Products) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Products{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", estimatedPreparaingTime=" + getEstimatedPreparaingTime() +
            "}";
    }
}
