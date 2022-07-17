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
 * A PermissionComposite.
 */
@Table("permission_composite")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "permissioncomposite")
public class PermissionComposite implements Serializable, Persistable<String> {

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
    private Permission permission;

    @Column("permission_id")
    private String permissionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public PermissionComposite id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public PermissionComposite description(String description) {
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

    public PermissionComposite setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Permission getPermission() {
        return this.permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
        this.permissionId = permission != null ? permission.getId() : null;
    }

    public PermissionComposite permission(Permission permission) {
        this.setPermission(permission);
        return this;
    }

    public String getPermissionId() {
        return this.permissionId;
    }

    public void setPermissionId(String permission) {
        this.permissionId = permission;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PermissionComposite)) {
            return false;
        }
        return id != null && id.equals(((PermissionComposite) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PermissionComposite{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
