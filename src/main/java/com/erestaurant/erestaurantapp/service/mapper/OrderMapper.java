package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Customer;
import com.erestaurant.erestaurantapp.domain.Order;
import com.erestaurant.erestaurantapp.domain.OrderType;
import com.erestaurant.erestaurantapp.domain.State;
import com.erestaurant.erestaurantapp.service.dto.CustomerDTO;
import com.erestaurant.erestaurantapp.service.dto.OrderDTO;
import com.erestaurant.erestaurantapp.service.dto.OrderTypeDTO;
import com.erestaurant.erestaurantapp.service.dto.StateDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Order} and its DTO {@link OrderDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {
    @Mapping(target = "type", source = "type", qualifiedByName = "orderTypeId")
    @Mapping(target = "state", source = "state", qualifiedByName = "stateId")
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    OrderDTO toDto(Order s);

    @Named("orderTypeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderTypeDTO toDtoOrderTypeId(OrderType orderType);

    @Named("stateId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    StateDTO toDtoStateId(State state);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);
}
