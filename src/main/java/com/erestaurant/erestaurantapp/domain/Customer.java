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
 * A Customer.
 */
@Table("customer")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "customer")
public class Customer implements Serializable, Persistable<String> {

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

    @Column("age")
    private Integer age;

    @Transient
    private boolean isPersisted;

    @Transient
    private CustomerProfile profile;

    @Transient
    @JsonIgnoreProperties(value = { "type", "state", "customer" }, allowSetters = true)
    private Set<Order> orders = new HashSet<>();

    @Column("profile_id")
    private String profileId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Customer id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Customer name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Customer description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return this.image;
    }

    public Customer image(byte[] image) {
        this.setImage(image);
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public Customer imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public String getEmail() {
        return this.email;
    }

    public Customer email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return this.age;
    }

    public Customer age(Integer age) {
        this.setAge(age);
        return this;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Customer setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public CustomerProfile getProfile() {
        return this.profile;
    }

    public void setProfile(CustomerProfile customerProfile) {
        this.profile = customerProfile;
        this.profileId = customerProfile != null ? customerProfile.getId() : null;
    }

    public Customer profile(CustomerProfile customerProfile) {
        this.setProfile(customerProfile);
        return this;
    }

    public Set<Order> getOrders() {
        return this.orders;
    }

    public void setOrders(Set<Order> orders) {
        if (this.orders != null) {
            this.orders.forEach(i -> i.setCustomer(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setCustomer(this));
        }
        this.orders = orders;
    }

    public Customer orders(Set<Order> orders) {
        this.setOrders(orders);
        return this;
    }

    public Customer addOrders(Order order) {
        this.orders.add(order);
        order.setCustomer(this);
        return this;
    }

    public Customer removeOrders(Order order) {
        this.orders.remove(order);
        order.setCustomer(null);
        return this;
    }

    public String getProfileId() {
        return this.profileId;
    }

    public void setProfileId(String customerProfile) {
        this.profileId = customerProfile;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return id != null && id.equals(((Customer) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", email='" + getEmail() + "'" +
            ", age=" + getAge() +
            "}";
    }
}
