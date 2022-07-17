package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Close;
import com.erestaurant.erestaurantapp.service.dto.CloseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Close} and its DTO {@link CloseDTO}.
 */
@Mapper(componentModel = "spring")
public interface CloseMapper extends EntityMapper<CloseDTO, Close> {}
