package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Customer;
import com.erestaurant.erestaurantapp.domain.CustomerProfile;
import com.erestaurant.erestaurantapp.service.dto.CustomerDTO;
import com.erestaurant.erestaurantapp.service.dto.CustomerProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Customer} and its DTO {@link CustomerDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper extends EntityMapper<CustomerDTO, Customer> {
    @Mapping(target = "profile", source = "profile", qualifiedByName = "customerProfileId")
    CustomerDTO toDto(Customer s);

    @Named("customerProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerProfileDTO toDtoCustomerProfileId(CustomerProfile customerProfile);
}
