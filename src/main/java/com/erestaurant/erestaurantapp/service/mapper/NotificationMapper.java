package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Notification;
import com.erestaurant.erestaurantapp.domain.NotificationType;
import com.erestaurant.erestaurantapp.domain.Restaurant;
import com.erestaurant.erestaurantapp.service.dto.NotificationDTO;
import com.erestaurant.erestaurantapp.service.dto.NotificationTypeDTO;
import com.erestaurant.erestaurantapp.service.dto.RestaurantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Mapping(target = "type", source = "type", qualifiedByName = "notificationTypeId")
    @Mapping(target = "restaurant", source = "restaurant", qualifiedByName = "restaurantId")
    NotificationDTO toDto(Notification s);

    @Named("notificationTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NotificationTypeDTO toDtoNotificationTypeId(NotificationType notificationType);

    @Named("restaurantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RestaurantDTO toDtoRestaurantId(Restaurant restaurant);
}
