package com.erestaurant.erestaurantapp.repository.rowmapper;

import com.erestaurant.erestaurantapp.domain.OrderType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link OrderType}, with proper type conversions.
 */
@Service
public class OrderTypeRowMapper implements BiFunction<Row, String, OrderType> {

    private final ColumnConverter converter;

    public OrderTypeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link OrderType} stored in the database.
     */
    @Override
    public OrderType apply(Row row, String prefix) {
        OrderType entity = new OrderType();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
