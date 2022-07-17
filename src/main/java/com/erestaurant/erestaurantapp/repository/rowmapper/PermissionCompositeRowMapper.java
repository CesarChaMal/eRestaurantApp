package com.erestaurant.erestaurantapp.repository.rowmapper;

import com.erestaurant.erestaurantapp.domain.PermissionComposite;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PermissionComposite}, with proper type conversions.
 */
@Service
public class PermissionCompositeRowMapper implements BiFunction<Row, String, PermissionComposite> {

    private final ColumnConverter converter;

    public PermissionCompositeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PermissionComposite} stored in the database.
     */
    @Override
    public PermissionComposite apply(Row row, String prefix) {
        PermissionComposite entity = new PermissionComposite();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setPermissionId(converter.fromRow(row, prefix + "_permission_id", String.class));
        return entity;
    }
}
