package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Restaurant;
import com.erestaurant.erestaurantapp.domain.RestaurantAd;
import com.erestaurant.erestaurantapp.service.dto.RestaurantAdDTO;
import com.erestaurant.erestaurantapp.service.dto.RestaurantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RestaurantAd} and its DTO {@link RestaurantAdDTO}.
 */
@Mapper(componentModel = "spring")
public interface RestaurantAdMapper extends EntityMapper<RestaurantAdDTO, RestaurantAd> {
    @Mapping(target = "restaurant", source = "restaurant", qualifiedByName = "restaurantId")
    RestaurantAdDTO toDto(RestaurantAd s);

    @Named("restaurantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RestaurantDTO toDtoRestaurantId(Restaurant restaurant);
}
