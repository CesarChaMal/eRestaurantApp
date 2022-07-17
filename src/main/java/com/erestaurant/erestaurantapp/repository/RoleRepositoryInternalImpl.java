package com.erestaurant.erestaurantapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.erestaurant.erestaurantapp.domain.Permission;
import com.erestaurant.erestaurantapp.domain.Role;
import com.erestaurant.erestaurantapp.repository.rowmapper.RoleRowMapper;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Role entity.
 */
@SuppressWarnings("unused")
class RoleRepositoryInternalImpl extends SimpleR2dbcRepository<Role, String> implements RoleRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RoleRowMapper roleMapper;

    private static final Table entityTable = Table.aliased("role", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable permissionsLink = new EntityManager.LinkTable(
        "rel_role__permissions",
        "role_id",
        "permissions_id"
    );

    public RoleRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RoleRowMapper roleMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Role.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.roleMapper = roleMapper;
    }

    @Override
    public Flux<Role> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Role> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = RoleSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Role.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Role> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Role> findById(String id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Role> findOneWithEagerRelationships(String id) {
        return findById(id);
    }

    @Override
    public Flux<Role> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Role> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Role process(Row row, RowMetadata metadata) {
        Role entity = roleMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Role> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Role> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(permissionsLink, entity.getId(), entity.getPermissions().stream().map(Permission::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(String entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(String entityId) {
        return entityManager.deleteFromLinkTable(permissionsLink, entityId);
    }
}
