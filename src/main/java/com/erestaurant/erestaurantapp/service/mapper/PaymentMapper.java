package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Cart;
import com.erestaurant.erestaurantapp.domain.Payment;
import com.erestaurant.erestaurantapp.service.dto.CartDTO;
import com.erestaurant.erestaurantapp.service.dto.PaymentDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Payment} and its DTO {@link PaymentDTO}.
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {
    @Mapping(target = "cart", source = "cart", qualifiedByName = "cartId")
    PaymentDTO toDto(Payment s);

    @Named("cartId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CartDTO toDtoCartId(Cart cart);
}
