package com.erestaurant.erestaurantapp.repository.rowmapper;

import com.erestaurant.erestaurantapp.domain.RestaurantDiscount;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RestaurantDiscount}, with proper type conversions.
 */
@Service
public class RestaurantDiscountRowMapper implements BiFunction<Row, String, RestaurantDiscount> {

    private final ColumnConverter converter;

    public RestaurantDiscountRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RestaurantDiscount} stored in the database.
     */
    @Override
    public RestaurantDiscount apply(Row row, String prefix) {
        RestaurantDiscount entity = new RestaurantDiscount();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setCode(converter.fromRow(row, prefix + "_code", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPercentage(converter.fromRow(row, prefix + "_percentage", Float.class));
        entity.setProductsId(converter.fromRow(row, prefix + "_products_id", String.class));
        return entity;
    }
}
