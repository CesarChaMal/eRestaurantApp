package com.erestaurant.erestaurantapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.erestaurant.erestaurantapp.domain.AppAd;
import com.erestaurant.erestaurantapp.repository.rowmapper.AdminRowMapper;
import com.erestaurant.erestaurantapp.repository.rowmapper.AppAdRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the AppAd entity.
 */
@SuppressWarnings("unused")
class AppAdRepositoryInternalImpl extends SimpleR2dbcRepository<AppAd, String> implements AppAdRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AdminRowMapper adminMapper;
    private final AppAdRowMapper appadMapper;

    private static final Table entityTable = Table.aliased("app_ad", EntityManager.ENTITY_ALIAS);
    private static final Table adminTable = Table.aliased("admin", "e_admin");

    public AppAdRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AdminRowMapper adminMapper,
        AppAdRowMapper appadMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(AppAd.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.adminMapper = adminMapper;
        this.appadMapper = appadMapper;
    }

    @Override
    public Flux<AppAd> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<AppAd> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AppAdSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AdminSqlHelper.getColumns(adminTable, "admin"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(adminTable)
            .on(Column.create("admin_id", entityTable))
            .equals(Column.create("id", adminTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, AppAd.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<AppAd> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<AppAd> findById(String id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private AppAd process(Row row, RowMetadata metadata) {
        AppAd entity = appadMapper.apply(row, "e");
        entity.setAdmin(adminMapper.apply(row, "admin"));
        return entity;
    }

    @Override
    public <S extends AppAd> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
