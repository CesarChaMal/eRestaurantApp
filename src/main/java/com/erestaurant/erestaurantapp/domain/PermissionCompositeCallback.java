package com.erestaurant.erestaurantapp.domain;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.event.AfterConvertCallback;
import org.springframework.data.r2dbc.mapping.event.AfterSaveCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class PermissionCompositeCallback implements AfterSaveCallback<PermissionComposite>, AfterConvertCallback<PermissionComposite> {

    @Override
    public Publisher<PermissionComposite> onAfterConvert(PermissionComposite entity, SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }

    @Override
    public Publisher<PermissionComposite> onAfterSave(PermissionComposite entity, OutboundRow outboundRow, SqlIdentifier table) {
        return Mono.just(entity.setIsPersisted());
    }
}
