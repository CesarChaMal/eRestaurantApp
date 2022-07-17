package com.erestaurant.erestaurantapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.erestaurant.erestaurantapp.domain.RestaurantDiscount;
import com.erestaurant.erestaurantapp.repository.rowmapper.ProductsRowMapper;
import com.erestaurant.erestaurantapp.repository.rowmapper.RestaurantDiscountRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the RestaurantDiscount entity.
 */
@SuppressWarnings("unused")
class RestaurantDiscountRepositoryInternalImpl
    extends SimpleR2dbcRepository<RestaurantDiscount, String>
    implements RestaurantDiscountRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProductsRowMapper productsMapper;
    private final RestaurantDiscountRowMapper restaurantdiscountMapper;

    private static final Table entityTable = Table.aliased("restaurant_discount", EntityManager.ENTITY_ALIAS);
    private static final Table productsTable = Table.aliased("products", "products");

    public RestaurantDiscountRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProductsRowMapper productsMapper,
        RestaurantDiscountRowMapper restaurantdiscountMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(RestaurantDiscount.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.productsMapper = productsMapper;
        this.restaurantdiscountMapper = restaurantdiscountMapper;
    }

    @Override
    public Flux<RestaurantDiscount> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<RestaurantDiscount> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = RestaurantDiscountSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProductsSqlHelper.getColumns(productsTable, "products"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(productsTable)
            .on(Column.create("products_id", entityTable))
            .equals(Column.create("id", productsTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, RestaurantDiscount.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<RestaurantDiscount> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<RestaurantDiscount> findById(String id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private RestaurantDiscount process(Row row, RowMetadata metadata) {
        RestaurantDiscount entity = restaurantdiscountMapper.apply(row, "e");
        entity.setProducts(productsMapper.apply(row, "products"));
        return entity;
    }

    @Override
    public <S extends RestaurantDiscount> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
