package com.erestaurant.erestaurantapp.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.erestaurant.erestaurantapp.domain.NotificationType} entity.
 */
public class NotificationTypeDTO implements Serializable {

    @NotNull(message = "must not be null")
    @Size(min = 5)
    private String id;

    @Lob
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationTypeDTO)) {
            return false;
        }

        NotificationTypeDTO notificationTypeDTO = (NotificationTypeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notificationTypeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationTypeDTO{" +
            "id='" + getId() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
