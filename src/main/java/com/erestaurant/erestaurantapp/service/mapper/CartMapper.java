package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Cart;
import com.erestaurant.erestaurantapp.domain.Order;
import com.erestaurant.erestaurantapp.service.dto.CartDTO;
import com.erestaurant.erestaurantapp.service.dto.OrderDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Cart} and its DTO {@link CartDTO}.
 */
@Mapper(componentModel = "spring")
public interface CartMapper extends EntityMapper<CartDTO, Cart> {
    @Mapping(target = "order", source = "order", qualifiedByName = "orderId")
    CartDTO toDto(Cart s);

    @Named("orderId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrderDTO toDtoOrderId(Order order);
}
