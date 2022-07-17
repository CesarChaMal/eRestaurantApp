package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Products;
import com.erestaurant.erestaurantapp.domain.RestaurantDiscount;
import com.erestaurant.erestaurantapp.service.dto.ProductsDTO;
import com.erestaurant.erestaurantapp.service.dto.RestaurantDiscountDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link RestaurantDiscount} and its DTO {@link RestaurantDiscountDTO}.
 */
@Mapper(componentModel = "spring")
public interface RestaurantDiscountMapper extends EntityMapper<RestaurantDiscountDTO, RestaurantDiscount> {
    @Mapping(target = "products", source = "products", qualifiedByName = "productsId")
    RestaurantDiscountDTO toDto(RestaurantDiscount s);

    @Named("productsId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProductsDTO toDtoProductsId(Products products);
}
