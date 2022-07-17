package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Cancel;
import com.erestaurant.erestaurantapp.service.dto.CancelDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cancel} and its DTO {@link CancelDTO}.
 */
@Mapper(componentModel = "spring")
public interface CancelMapper extends EntityMapper<CancelDTO, Cancel> {}
