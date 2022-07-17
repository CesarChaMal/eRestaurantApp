package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.NotificationType;
import com.erestaurant.erestaurantapp.service.dto.NotificationTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link NotificationType} and its DTO {@link NotificationTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationTypeMapper extends EntityMapper<NotificationTypeDTO, NotificationType> {}
