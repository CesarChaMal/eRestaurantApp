package com.erestaurant.erestaurantapp.repository.rowmapper;

import com.erestaurant.erestaurantapp.domain.AppAd;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link AppAd}, with proper type conversions.
 */
@Service
public class AppAdRowMapper implements BiFunction<Row, String, AppAd> {

    private final ColumnConverter converter;

    public AppAdRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link AppAd} stored in the database.
     */
    @Override
    public AppAd apply(Row row, String prefix) {
        AppAd entity = new AppAd();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setUrl(converter.fromRow(row, prefix + "_url", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setAdminId(converter.fromRow(row, prefix + "_admin_id", String.class));
        return entity;
    }
}
