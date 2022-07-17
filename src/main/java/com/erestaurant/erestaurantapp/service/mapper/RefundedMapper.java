package com.erestaurant.erestaurantapp.service.mapper;

import com.erestaurant.erestaurantapp.domain.Refunded;
import com.erestaurant.erestaurantapp.service.dto.RefundedDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Refunded} and its DTO {@link RefundedDTO}.
 */
@Mapper(componentModel = "spring")
public interface RefundedMapper extends EntityMapper<RefundedDTO, Refunded> {}
