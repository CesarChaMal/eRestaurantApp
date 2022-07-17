package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.AppUser;
import com.erestaurant.erestaurantapp.domain.Role;
import com.erestaurant.erestaurantapp.domain.User;
import com.erestaurant.erestaurantapp.service.dto.AppUserDTO;
import com.erestaurant.erestaurantapp.service.dto.RoleDTO;
import com.erestaurant.erestaurantapp.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppUser} and its DTO {@link AppUserDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppUserMapper extends EntityMapper<AppUserDTO, AppUser> {
    @Mapping(target = "internalUser", source = "internalUser", qualifiedByName = "userLogin")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "roleIdSet")
    AppUserDTO toDto(AppUser s);

    @Mapping(target = "removeRoles", ignore = true)
    AppUser toEntity(AppUserDTO appUserDTO);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("roleId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RoleDTO toDtoRoleId(Role role);

    @Named("roleIdSet")
    default Set<RoleDTO> toDtoRoleIdSet(Set<Role> role) {
        return role.stream().map(this::toDtoRoleId).collect(Collectors.toSet());
    }
}
