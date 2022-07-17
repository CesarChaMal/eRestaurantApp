package com.erestaurant.erestaurantapp.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.erestaurant.erestaurantapp.domain.Products;
import com.erestaurant.erestaurantapp.repository.rowmapper.CartRowMapper;
import com.erestaurant.erestaurantapp.repository.rowmapper.CategoriesRowMapper;
import com.erestaurant.erestaurantapp.repository.rowmapper.ProductsRowMapper;
import com.erestaurant.erestaurantapp.repository.rowmapper.RestaurantRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Products entity.
 */
@SuppressWarnings("unused")
class ProductsRepositoryInternalImpl extends SimpleR2dbcRepository<Products, String> implements ProductsRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CategoriesRowMapper categoriesMapper;
    private final RestaurantRowMapper restaurantMapper;
    private final CartRowMapper cartMapper;
    private final ProductsRowMapper productsMapper;

    private static final Table entityTable = Table.aliased("products", EntityManager.ENTITY_ALIAS);
    private static final Table categoryTable = Table.aliased("categories", "category");
    private static final Table restaurantTable = Table.aliased("restaurant", "restaurant");
    private static final Table cartTable = Table.aliased("cart", "cart");

    public ProductsRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CategoriesRowMapper categoriesMapper,
        RestaurantRowMapper restaurantMapper,
        CartRowMapper cartMapper,
        ProductsRowMapper productsMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Products.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.categoriesMapper = categoriesMapper;
        this.restaurantMapper = restaurantMapper;
        this.cartMapper = cartMapper;
        this.productsMapper = productsMapper;
    }

    @Override
    public Flux<Products> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Products> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProductsSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CategoriesSqlHelper.getColumns(categoryTable, "category"));
        columns.addAll(RestaurantSqlHelper.getColumns(restaurantTable, "restaurant"));
        columns.addAll(CartSqlHelper.getColumns(cartTable, "cart"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(categoryTable)
            .on(Column.create("category_id", entityTable))
            .equals(Column.create("id", categoryTable))
            .leftOuterJoin(restaurantTable)
            .on(Column.create("restaurant_id", entityTable))
            .equals(Column.create("id", restaurantTable))
            .leftOuterJoin(cartTable)
            .on(Column.create("cart_id", entityTable))
            .equals(Column.create("id", cartTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Products.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Products> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Products> findById(String id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Products process(Row row, RowMetadata metadata) {
        Products entity = productsMapper.apply(row, "e");
        entity.setCategory(categoriesMapper.apply(row, "category"));
        entity.setRestaurant(restaurantMapper.apply(row, "restaurant"));
        entity.setCart(cartMapper.apply(row, "cart"));
        return entity;
    }

    @Override
    public <S extends Products> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
