package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.OrderType;
import com.erestaurant.erestaurantapp.service.dto.OrderTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderType} and its DTO {@link OrderTypeDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderTypeMapper extends EntityMapper<OrderTypeDTO, OrderType> {}
