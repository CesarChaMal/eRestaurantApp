package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.SimplePermission;
import com.erestaurant.erestaurantapp.service.dto.SimplePermissionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link SimplePermission} and its DTO {@link SimplePermissionDTO}.
 */
@Mapper(componentModel = "spring")
public interface SimplePermissionMapper extends EntityMapper<SimplePermissionDTO, SimplePermission> {}
