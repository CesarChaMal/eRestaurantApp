package com.erestaurant.erestaurantapp.repository.rowmapper;

import com.erestaurant.erestaurantapp.domain.Profile;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Profile}, with proper type conversions.
 */
@Service
public class ProfileRowMapper implements BiFunction<Row, String, Profile> {

    private final ColumnConverter converter;

    public ProfileRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Profile} stored in the database.
     */
    @Override
    public Profile apply(Row row, String prefix) {
        Profile entity = new Profile();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        return entity;
    }
}
