package com.erestaurant.erestaurantapp.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.erestaurant.erestaurantapp.domain.RestaurantAd} entity.
 */
public class RestaurantAdDTO implements Serializable {

    @NotNull(message = "must not be null")
    @Size(min = 5)
    private String id;

    @NotNull(message = "must not be null")
    @Size(min = 5)
    private String url;

    @Lob
    private String description;

    private RestaurantDTO restaurant;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RestaurantDTO getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantDTO restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RestaurantAdDTO)) {
            return false;
        }

        RestaurantAdDTO restaurantAdDTO = (RestaurantAdDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, restaurantAdDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RestaurantAdDTO{" +
            "id='" + getId() + "'" +
            ", url='" + getUrl() + "'" +
            ", description='" + getDescription() + "'" +
            ", restaurant=" + getRestaurant() +
            "}";
    }
}
