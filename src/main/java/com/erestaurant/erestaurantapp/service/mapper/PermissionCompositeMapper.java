package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Permission;
import com.erestaurant.erestaurantapp.domain.PermissionComposite;
import com.erestaurant.erestaurantapp.service.dto.PermissionCompositeDTO;
import com.erestaurant.erestaurantapp.service.dto.PermissionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PermissionComposite} and its DTO {@link PermissionCompositeDTO}.
 */
@Mapper(componentModel = "spring")
public interface PermissionCompositeMapper extends EntityMapper<PermissionCompositeDTO, PermissionComposite> {
    @Mapping(target = "permission", source = "permission", qualifiedByName = "permissionId")
    PermissionCompositeDTO toDto(PermissionComposite s);

    @Named("permissionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PermissionDTO toDtoPermissionId(Permission permission);
}
