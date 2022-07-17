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
 * A Role.
 */
@Table("role")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "role")
public class Role implements Serializable, Persistable<String> {

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
    @JsonIgnoreProperties(value = { "roles" }, allowSetters = true)
    private Set<Permission> permissions = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "internalUser", "roles" }, allowSetters = true)
    private Set<AppUser> users = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public Role id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public Role description(String description) {
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

    public Role setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<Permission> getPermissions() {
        return this.permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Role permissions(Set<Permission> permissions) {
        this.setPermissions(permissions);
        return this;
    }

    public Role addPermissions(Permission permission) {
        this.permissions.add(permission);
        permission.getRoles().add(this);
        return this;
    }

    public Role removePermissions(Permission permission) {
        this.permissions.remove(permission);
        permission.getRoles().remove(this);
        return this;
    }

    public Set<AppUser> getUsers() {
        return this.users;
    }

    public void setUsers(Set<AppUser> appUsers) {
        if (this.users != null) {
            this.users.forEach(i -> i.removeRoles(this));
        }
        if (appUsers != null) {
            appUsers.forEach(i -> i.addRoles(this));
        }
        this.users = appUsers;
    }

    public Role users(Set<AppUser> appUsers) {
        this.setUsers(appUsers);
        return this;
    }

    public Role addUsers(AppUser appUser) {
        this.users.add(appUser);
        appUser.getRoles().add(this);
        return this;
    }

    public Role removeUsers(AppUser appUser) {
        this.users.remove(appUser);
        appUser.getRoles().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Role)) {
            return false;
        }
        return id != null && id.equals(((Role) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Role{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
