package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.NewOrder;
import com.erestaurant.erestaurantapp.service.dto.NewOrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link NewOrder} and its DTO {@link NewOrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface NewOrderMapper extends EntityMapper<NewOrderDTO, NewOrder> {}
