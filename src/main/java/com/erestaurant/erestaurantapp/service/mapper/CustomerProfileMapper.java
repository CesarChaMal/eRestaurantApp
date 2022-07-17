package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.CustomerProfile;
import com.erestaurant.erestaurantapp.service.dto.CustomerProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CustomerProfile} and its DTO {@link CustomerProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerProfileMapper extends EntityMapper<CustomerProfileDTO, CustomerProfile> {}
