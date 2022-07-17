package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Employee;
import com.erestaurant.erestaurantapp.domain.Restaurant;
import com.erestaurant.erestaurantapp.service.dto.EmployeeDTO;
import com.erestaurant.erestaurantapp.service.dto.RestaurantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Employee} and its DTO {@link EmployeeDTO}.
 */
@Mapper(componentModel = "spring")
public interface EmployeeMapper extends EntityMapper<EmployeeDTO, Employee> {
    @Mapping(target = "restaurant", source = "restaurant", qualifiedByName = "restaurantId")
    EmployeeDTO toDto(Employee s);

    @Named("restaurantId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    RestaurantDTO toDtoRestaurantId(Restaurant restaurant);
}
