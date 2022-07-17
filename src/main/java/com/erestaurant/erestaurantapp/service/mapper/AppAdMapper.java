package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Admin;
import com.erestaurant.erestaurantapp.domain.AppAd;
import com.erestaurant.erestaurantapp.service.dto.AdminDTO;
import com.erestaurant.erestaurantapp.service.dto.AppAdDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppAd} and its DTO {@link AppAdDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppAdMapper extends EntityMapper<AppAdDTO, AppAd> {
    @Mapping(target = "admin", source = "admin", qualifiedByName = "adminId")
    AppAdDTO toDto(AppAd s);

    @Named("adminId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AdminDTO toDtoAdminId(Admin admin);
}
