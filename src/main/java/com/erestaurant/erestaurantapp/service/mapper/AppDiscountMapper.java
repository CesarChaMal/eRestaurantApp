package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.AppDiscount;
import com.erestaurant.erestaurantapp.domain.Cart;
import com.erestaurant.erestaurantapp.service.dto.AppDiscountDTO;
import com.erestaurant.erestaurantapp.service.dto.CartDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppDiscount} and its DTO {@link AppDiscountDTO}.
 */
@Mapper(componentModel = "spring")
public interface AppDiscountMapper extends EntityMapper<AppDiscountDTO, AppDiscount> {
    @Mapping(target = "cart", source = "cart", qualifiedByName = "cartId")
    AppDiscountDTO toDto(AppDiscount s);

    @Named("cartId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CartDTO toDtoCartId(Cart cart);
}
