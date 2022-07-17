package com.erestaurant.erestaurantapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.erestaurant.erestaurantapp.domain.PermissionComposite;
import com.erestaurant.erestaurantapp.repository.rowmapper.PermissionCompositeRowMapper;
import com.erestaurant.erestaurantapp.repository.rowmapper.PermissionRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the PermissionComposite entity.
 */
@SuppressWarnings("unused")
class PermissionCompositeRepositoryInternalImpl
    extends SimpleR2dbcRepository<PermissionComposite, String>
    implements PermissionCompositeRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PermissionRowMapper permissionMapper;
    private final PermissionCompositeRowMapper permissioncompositeMapper;

    private static final Table entityTable = Table.aliased("permission_composite", EntityManager.ENTITY_ALIAS);
    private static final Table permissionTable = Table.aliased("permission", "permission");

    public PermissionCompositeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PermissionRowMapper permissionMapper,
        PermissionCompositeRowMapper permissioncompositeMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(PermissionComposite.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.permissionMapper = permissionMapper;
        this.permissioncompositeMapper = permissioncompositeMapper;
    }

    @Override
    public Flux<PermissionComposite> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<PermissionComposite> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = PermissionCompositeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PermissionSqlHelper.getColumns(permissionTable, "permission"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(permissionTable)
            .on(Column.create("permission_id", entityTable))
            .equals(Column.create("id", permissionTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, PermissionComposite.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<PermissionComposite> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<PermissionComposite> findById(String id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private PermissionComposite process(Row row, RowMetadata metadata) {
        PermissionComposite entity = permissioncompositeMapper.apply(row, "e");
        entity.setPermission(permissionMapper.apply(row, "permission"));
        return entity;
    }

    @Override
    public <S extends PermissionComposite> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
