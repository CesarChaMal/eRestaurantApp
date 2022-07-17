package com.erestaurant.erestaurantapp.repository.rowmapper;

import com.erestaurant.erestaurantapp.domain.RestaurantAd;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RestaurantAd}, with proper type conversions.
 */
@Service
public class RestaurantAdRowMapper implements BiFunction<Row, String, RestaurantAd> {

    private final ColumnConverter converter;

    public RestaurantAdRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RestaurantAd} stored in the database.
     */
    @Override
    public RestaurantAd apply(Row row, String prefix) {
        RestaurantAd entity = new RestaurantAd();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setUrl(converter.fromRow(row, prefix + "_url", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setRestaurantId(converter.fromRow(row, prefix + "_restaurant_id", String.class));
        return entity;
    }
}
