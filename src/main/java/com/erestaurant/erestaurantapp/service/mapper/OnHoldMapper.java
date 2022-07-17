package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.OnHold;
import com.erestaurant.erestaurantapp.service.dto.OnHoldDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OnHold} and its DTO {@link OnHoldDTO}.
 */
@Mapper(componentModel = "spring")
public interface OnHoldMapper extends EntityMapper<OnHoldDTO, OnHold> {}
