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
 * A Restaurant.
 */
@Table("restaurant")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "restaurant")
public class Restaurant implements Serializable, Persistable<String> {

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

    @Column("email")
    private String email;

    @NotNull(message = "must not be null")
    @Column("rating")
    private Float rating;

    @Transient
    private boolean isPersisted;

    @Transient
    @JsonIgnoreProperties(value = { "category", "discounts", "restaurant", "cart" }, allowSetters = true)
    private Set<Products> products = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "restaurant" }, allowSetters = true)
    private Set<RestaurantAd> ads = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "restaurant" }, allowSetters = true)
    private Set<Employee> employees = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "type", "restaurant" }, allowSetters = true)
    private Set<Notification> notifications = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Restaurant id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Restaurant name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Restaurant description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return this.image;
    }

    public Restaurant image(byte[] image) {
        this.setImage(image);
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public Restaurant imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public String getEmail() {
        return this.email;
    }

    public Restaurant email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Float getRating() {
        return this.rating;
    }

    public Restaurant rating(Float rating) {
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

    public Restaurant setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<Products> getProducts() {
        return this.products;
    }

    public void setProducts(Set<Products> products) {
        if (this.products != null) {
            this.products.forEach(i -> i.setRestaurant(null));
        }
        if (products != null) {
            products.forEach(i -> i.setRestaurant(this));
        }
        this.products = products;
    }

    public Restaurant products(Set<Products> products) {
        this.setProducts(products);
        return this;
    }

    public Restaurant addProducts(Products products) {
        this.products.add(products);
        products.setRestaurant(this);
        return this;
    }

    public Restaurant removeProducts(Products products) {
        this.products.remove(products);
        products.setRestaurant(null);
        return this;
    }

    public Set<RestaurantAd> getAds() {
        return this.ads;
    }

    public void setAds(Set<RestaurantAd> restaurantAds) {
        if (this.ads != null) {
            this.ads.forEach(i -> i.setRestaurant(null));
        }
        if (restaurantAds != null) {
            restaurantAds.forEach(i -> i.setRestaurant(this));
        }
        this.ads = restaurantAds;
    }

    public Restaurant ads(Set<RestaurantAd> restaurantAds) {
        this.setAds(restaurantAds);
        return this;
    }

    public Restaurant addAds(RestaurantAd restaurantAd) {
        this.ads.add(restaurantAd);
        restaurantAd.setRestaurant(this);
        return this;
    }

    public Restaurant removeAds(RestaurantAd restaurantAd) {
        this.ads.remove(restaurantAd);
        restaurantAd.setRestaurant(null);
        return this;
    }

    public Set<Employee> getEmployees() {
        return this.employees;
    }

    public void setEmployees(Set<Employee> employees) {
        if (this.employees != null) {
            this.employees.forEach(i -> i.setRestaurant(null));
        }
        if (employees != null) {
            employees.forEach(i -> i.setRestaurant(this));
        }
        this.employees = employees;
    }

    public Restaurant employees(Set<Employee> employees) {
        this.setEmployees(employees);
        return this;
    }

    public Restaurant addEmployees(Employee employee) {
        this.employees.add(employee);
        employee.setRestaurant(this);
        return this;
    }

    public Restaurant removeEmployees(Employee employee) {
        this.employees.remove(employee);
        employee.setRestaurant(null);
        return this;
    }

    public Set<Notification> getNotifications() {
        return this.notifications;
    }

    public void setNotifications(Set<Notification> notifications) {
        if (this.notifications != null) {
            this.notifications.forEach(i -> i.setRestaurant(null));
        }
        if (notifications != null) {
            notifications.forEach(i -> i.setRestaurant(this));
        }
        this.notifications = notifications;
    }

    public Restaurant notifications(Set<Notification> notifications) {
        this.setNotifications(notifications);
        return this;
    }

    public Restaurant addNotifications(Notification notification) {
        this.notifications.add(notification);
        notification.setRestaurant(this);
        return this;
    }

    public Restaurant removeNotifications(Notification notification) {
        this.notifications.remove(notification);
        notification.setRestaurant(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Restaurant)) {
            return false;
        }
        return id != null && id.equals(((Restaurant) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Restaurant{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", email='" + getEmail() + "'" +
            ", rating=" + getRating() +
            "}";
    }
}
