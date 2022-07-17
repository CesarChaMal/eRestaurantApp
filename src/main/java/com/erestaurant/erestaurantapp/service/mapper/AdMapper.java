package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Ad;
import com.erestaurant.erestaurantapp.service.dto.AdDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Ad} and its DTO {@link AdDTO}.
 */
@Mapper(componentModel = "spring")
public interface AdMapper extends EntityMapper<AdDTO, Ad> {}
