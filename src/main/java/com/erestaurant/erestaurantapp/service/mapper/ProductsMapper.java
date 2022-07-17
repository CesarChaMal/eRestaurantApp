package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Cart;
import com.erestaurant.erestaurantapp.domain.Categories;
import com.erestaurant.erestaurantapp.domain.Products;
import com.erestaurant.erestaurantapp.domain.Restaurant;
import com.erestaurant.erestaurantapp.service.dto.CartDTO;
import com.erestaurant.erestaurantapp.service.dto.CategoriesDTO;
import com.erestaurant.erestaurantapp.service.dto.ProductsDTO;
import com.erestaurant.erestaurantapp.service.dto.RestaurantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Products} and its DTO {@link ProductsDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductsMapper extends EntityMapper<ProductsDTO, Products> {
    @Mapping(target = "category", source = "category", qualifiedByName = "categoriesId")
    @Mapping(target = "restaurant", source = "restaurant", qualifiedByName = "restaurantId")
    @Mapping(target = "cart", source = "cart", qualifiedByName = "cartId")
    ProductsDTO toDto(Products s);

    @Named("categoriesId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CategoriesDTO toDtoCategoriesId(Categories categories);

    @Named("restaurantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RestaurantDTO toDtoRestaurantId(Restaurant restaurant);

    @Named("cartId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CartDTO toDtoCartId(Cart cart);
}
