package com.erestaurant.erestaurantapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.erestaurantapp.IntegrationTest;
import com.erestaurant.erestaurantapp.domain.OrderType;
import com.erestaurant.erestaurantapp.repository.EntityManager;
import com.erestaurant.erestaurantapp.repository.OrderTypeRepository;
import com.erestaurant.erestaurantapp.repository.search.OrderTypeSearchRepository;
import com.erestaurant.erestaurantapp.service.dto.OrderTypeDTO;
import com.erestaurant.erestaurantapp.service.mapper.OrderTypeMapper;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link OrderTypeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OrderTypeResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/order-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/order-types";

    @Autowired
    private OrderTypeRepository orderTypeRepository;

    @Autowired
    private OrderTypeMapper orderTypeMapper;

    /**
     * This repository is mocked in the com.erestaurant.erestaurantapp.repository.search test package.
     *
     * @see com.erestaurant.erestaurantapp.repository.search.OrderTypeSearchRepositoryMockConfiguration
     */
    @Autowired
    private OrderTypeSearchRepository mockOrderTypeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private OrderType orderType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderType createEntity(EntityManager em) {
        OrderType orderType = new OrderType().description(DEFAULT_DESCRIPTION);
        return orderType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderType createUpdatedEntity(EntityManager em) {
        OrderType orderType = new OrderType().description(UPDATED_DESCRIPTION);
        return orderType;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(OrderType.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        orderType = createEntity(em);
    }

    @Test
    void createOrderType() throws Exception {
        int databaseSizeBeforeCreate = orderTypeRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockOrderTypeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the OrderType
        OrderTypeDTO orderTypeDTO = orderTypeMapper.toDto(orderType);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderTypeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the OrderType in the database
        List<OrderType> orderTypeList = orderTypeRepository.findAll().collectList().block();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeCreate + 1);
        OrderType testOrderType = orderTypeList.get(orderTypeList.size() - 1);
        assertThat(testOrderType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the OrderType in Elasticsearch
        verify(mockOrderTypeSearchRepository, times(1)).save(testOrderType);
    }

    @Test
    void createOrderTypeWithExistingId() throws Exception {
        // Create the OrderType with an existing ID
        orderType.setId("existing_id");
        OrderTypeDTO orderTypeDTO = orderTypeMapper.toDto(orderType);

        int databaseSizeBeforeCreate = orderTypeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderType in the database
        List<OrderType> orderTypeList = orderTypeRepository.findAll().collectList().block();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeCreate);

        // Validate the OrderType in Elasticsearch
        verify(mockOrderTypeSearchRepository, times(0)).save(orderType);
    }

    @Test
    void getAllOrderTypesAsStream() {
        // Initialize the database
        orderType.setId(UUID.randomUUID().toString());
        orderTypeRepository.save(orderType).block();

        List<OrderType> orderTypeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(OrderTypeDTO.class)
            .getResponseBody()
            .map(orderTypeMapper::toEntity)
            .filter(orderType::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(orderTypeList).isNotNull();
        assertThat(orderTypeList).hasSize(1);
        OrderType testOrderType = orderTypeList.get(0);
        assertThat(testOrderType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllOrderTypes() {
        // Initialize the database
        orderType.setId(UUID.randomUUID().toString());
        orderTypeRepository.save(orderType).block();

        // Get all the orderTypeList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(orderType.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getOrderType() {
        // Initialize the database
        orderType.setId(UUID.randomUUID().toString());
        orderTypeRepository.save(orderType).block();

        // Get the orderType
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, orderType.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(orderType.getId()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getNonExistingOrderType() {
        // Get the orderType
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewOrderType() throws Exception {
        // Configure the mock search repository
        when(mockOrderTypeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        orderType.setId(UUID.randomUUID().toString());
        orderTypeRepository.save(orderType).block();

        int databaseSizeBeforeUpdate = orderTypeRepository.findAll().collectList().block().size();

        // Update the orderType
        OrderType updatedOrderType = orderTypeRepository.findById(orderType.getId()).block();
        updatedOrderType.description(UPDATED_DESCRIPTION);
        OrderTypeDTO orderTypeDTO = orderTypeMapper.toDto(updatedOrderType);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orderTypeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderTypeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderType in the database
        List<OrderType> orderTypeList = orderTypeRepository.findAll().collectList().block();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeUpdate);
        OrderType testOrderType = orderTypeList.get(orderTypeList.size() - 1);
        assertThat(testOrderType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the OrderType in Elasticsearch
        verify(mockOrderTypeSearchRepository).save(testOrderType);
    }

    @Test
    void putNonExistingOrderType() throws Exception {
        int databaseSizeBeforeUpdate = orderTypeRepository.findAll().collectList().block().size();
        orderType.setId(UUID.randomUUID().toString());

        // Create the OrderType
        OrderTypeDTO orderTypeDTO = orderTypeMapper.toDto(orderType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orderTypeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderType in the database
        List<OrderType> orderTypeList = orderTypeRepository.findAll().collectList().block();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrderType in Elasticsearch
        verify(mockOrderTypeSearchRepository, times(0)).save(orderType);
    }

    @Test
    void putWithIdMismatchOrderType() throws Exception {
        int databaseSizeBeforeUpdate = orderTypeRepository.findAll().collectList().block().size();
        orderType.setId(UUID.randomUUID().toString());

        // Create the OrderType
        OrderTypeDTO orderTypeDTO = orderTypeMapper.toDto(orderType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderType in the database
        List<OrderType> orderTypeList = orderTypeRepository.findAll().collectList().block();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrderType in Elasticsearch
        verify(mockOrderTypeSearchRepository, times(0)).save(orderType);
    }

    @Test
    void putWithMissingIdPathParamOrderType() throws Exception {
        int databaseSizeBeforeUpdate = orderTypeRepository.findAll().collectList().block().size();
        orderType.setId(UUID.randomUUID().toString());

        // Create the OrderType
        OrderTypeDTO orderTypeDTO = orderTypeMapper.toDto(orderType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderTypeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrderType in the database
        List<OrderType> orderTypeList = orderTypeRepository.findAll().collectList().block();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrderType in Elasticsearch
        verify(mockOrderTypeSearchRepository, times(0)).save(orderType);
    }

    @Test
    void partialUpdateOrderTypeWithPatch() throws Exception {
        // Initialize the database
        orderType.setId(UUID.randomUUID().toString());
        orderTypeRepository.save(orderType).block();

        int databaseSizeBeforeUpdate = orderTypeRepository.findAll().collectList().block().size();

        // Update the orderType using partial update
        OrderType partialUpdatedOrderType = new OrderType();
        partialUpdatedOrderType.setId(orderType.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrderType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderType in the database
        List<OrderType> orderTypeList = orderTypeRepository.findAll().collectList().block();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeUpdate);
        OrderType testOrderType = orderTypeList.get(orderTypeList.size() - 1);
        assertThat(testOrderType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void fullUpdateOrderTypeWithPatch() throws Exception {
        // Initialize the database
        orderType.setId(UUID.randomUUID().toString());
        orderTypeRepository.save(orderType).block();

        int databaseSizeBeforeUpdate = orderTypeRepository.findAll().collectList().block().size();

        // Update the orderType using partial update
        OrderType partialUpdatedOrderType = new OrderType();
        partialUpdatedOrderType.setId(orderType.getId());

        partialUpdatedOrderType.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrderType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderType in the database
        List<OrderType> orderTypeList = orderTypeRepository.findAll().collectList().block();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeUpdate);
        OrderType testOrderType = orderTypeList.get(orderTypeList.size() - 1);
        assertThat(testOrderType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingOrderType() throws Exception {
        int databaseSizeBeforeUpdate = orderTypeRepository.findAll().collectList().block().size();
        orderType.setId(UUID.randomUUID().toString());

        // Create the OrderType
        OrderTypeDTO orderTypeDTO = orderTypeMapper.toDto(orderType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, orderTypeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderType in the database
        List<OrderType> orderTypeList = orderTypeRepository.findAll().collectList().block();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrderType in Elasticsearch
        verify(mockOrderTypeSearchRepository, times(0)).save(orderType);
    }

    @Test
    void patchWithIdMismatchOrderType() throws Exception {
        int databaseSizeBeforeUpdate = orderTypeRepository.findAll().collectList().block().size();
        orderType.setId(UUID.randomUUID().toString());

        // Create the OrderType
        OrderTypeDTO orderTypeDTO = orderTypeMapper.toDto(orderType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderType in the database
        List<OrderType> orderTypeList = orderTypeRepository.findAll().collectList().block();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrderType in Elasticsearch
        verify(mockOrderTypeSearchRepository, times(0)).save(orderType);
    }

    @Test
    void patchWithMissingIdPathParamOrderType() throws Exception {
        int databaseSizeBeforeUpdate = orderTypeRepository.findAll().collectList().block().size();
        orderType.setId(UUID.randomUUID().toString());

        // Create the OrderType
        OrderTypeDTO orderTypeDTO = orderTypeMapper.toDto(orderType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderTypeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrderType in the database
        List<OrderType> orderTypeList = orderTypeRepository.findAll().collectList().block();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the OrderType in Elasticsearch
        verify(mockOrderTypeSearchRepository, times(0)).save(orderType);
    }

    @Test
    void deleteOrderType() {
        // Configure the mock search repository
        when(mockOrderTypeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockOrderTypeSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        orderType.setId(UUID.randomUUID().toString());
        orderTypeRepository.save(orderType).block();

        int databaseSizeBeforeDelete = orderTypeRepository.findAll().collectList().block().size();

        // Delete the orderType
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, orderType.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<OrderType> orderTypeList = orderTypeRepository.findAll().collectList().block();
        assertThat(orderTypeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the OrderType in Elasticsearch
        verify(mockOrderTypeSearchRepository, times(1)).deleteById(orderType.getId());
    }

    @Test
    void searchOrderType() {
        // Configure the mock search repository
        when(mockOrderTypeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        orderType.setId(UUID.randomUUID().toString());
        orderTypeRepository.save(orderType).block();
        when(mockOrderTypeSearchRepository.search("id:" + orderType.getId())).thenReturn(Flux.just(orderType));

        // Search the orderType
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + orderType.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(orderType.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }
}
