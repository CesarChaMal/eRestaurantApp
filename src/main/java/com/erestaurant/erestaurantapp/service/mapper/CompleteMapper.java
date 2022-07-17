package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Complete;
import com.erestaurant.erestaurantapp.service.dto.CompleteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Complete} and its DTO {@link CompleteDTO}.
 */
@Mapper(componentModel = "spring")
public interface CompleteMapper extends EntityMapper<CompleteDTO, Complete> {}
