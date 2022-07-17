package com.erestaurant.erestaurantapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.erestaurantapp.IntegrationTest;
import com.erestaurant.erestaurantapp.domain.RestaurantDiscount;
import com.erestaurant.erestaurantapp.repository.EntityManager;
import com.erestaurant.erestaurantapp.repository.RestaurantDiscountRepository;
import com.erestaurant.erestaurantapp.repository.search.RestaurantDiscountSearchRepository;
import com.erestaurant.erestaurantapp.service.dto.RestaurantDiscountDTO;
import com.erestaurant.erestaurantapp.service.mapper.RestaurantDiscountMapper;
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
 * Integration tests for the {@link RestaurantDiscountResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RestaurantDiscountResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Float DEFAULT_PERCENTAGE = 1F;
    private static final Float UPDATED_PERCENTAGE = 2F;

    private static final String ENTITY_API_URL = "/api/restaurant-discounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/restaurant-discounts";

    @Autowired
    private RestaurantDiscountRepository restaurantDiscountRepository;

    @Autowired
    private RestaurantDiscountMapper restaurantDiscountMapper;

    /**
     * This repository is mocked in the com.erestaurant.erestaurantapp.repository.search test package.
     *
     * @see com.erestaurant.erestaurantapp.repository.search.RestaurantDiscountSearchRepositoryMockConfiguration
     */
    @Autowired
    private RestaurantDiscountSearchRepository mockRestaurantDiscountSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RestaurantDiscount restaurantDiscount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RestaurantDiscount createEntity(EntityManager em) {
        RestaurantDiscount restaurantDiscount = new RestaurantDiscount()
            .code(DEFAULT_CODE)
            .description(DEFAULT_DESCRIPTION)
            .percentage(DEFAULT_PERCENTAGE);
        return restaurantDiscount;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RestaurantDiscount createUpdatedEntity(EntityManager em) {
        RestaurantDiscount restaurantDiscount = new RestaurantDiscount()
            .code(UPDATED_CODE)
            .description(UPDATED_DESCRIPTION)
            .percentage(UPDATED_PERCENTAGE);
        return restaurantDiscount;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RestaurantDiscount.class).block();
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
        restaurantDiscount = createEntity(em);
    }

    @Test
    void createRestaurantDiscount() throws Exception {
        int databaseSizeBeforeCreate = restaurantDiscountRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockRestaurantDiscountSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the RestaurantDiscount
        RestaurantDiscountDTO restaurantDiscountDTO = restaurantDiscountMapper.toDto(restaurantDiscount);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDiscountDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RestaurantDiscount in the database
        List<RestaurantDiscount> restaurantDiscountList = restaurantDiscountRepository.findAll().collectList().block();
        assertThat(restaurantDiscountList).hasSize(databaseSizeBeforeCreate + 1);
        RestaurantDiscount testRestaurantDiscount = restaurantDiscountList.get(restaurantDiscountList.size() - 1);
        assertThat(testRestaurantDiscount.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testRestaurantDiscount.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRestaurantDiscount.getPercentage()).isEqualTo(DEFAULT_PERCENTAGE);

        // Validate the RestaurantDiscount in Elasticsearch
        verify(mockRestaurantDiscountSearchRepository, times(1)).save(testRestaurantDiscount);
    }

    @Test
    void createRestaurantDiscountWithExistingId() throws Exception {
        // Create the RestaurantDiscount with an existing ID
        restaurantDiscount.setId("existing_id");
        RestaurantDiscountDTO restaurantDiscountDTO = restaurantDiscountMapper.toDto(restaurantDiscount);

        int databaseSizeBeforeCreate = restaurantDiscountRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RestaurantDiscount in the database
        List<RestaurantDiscount> restaurantDiscountList = restaurantDiscountRepository.findAll().collectList().block();
        assertThat(restaurantDiscountList).hasSize(databaseSizeBeforeCreate);

        // Validate the RestaurantDiscount in Elasticsearch
        verify(mockRestaurantDiscountSearchRepository, times(0)).save(restaurantDiscount);
    }

    @Test
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = restaurantDiscountRepository.findAll().collectList().block().size();
        // set the field null
        restaurantDiscount.setCode(null);

        // Create the RestaurantDiscount, which fails.
        RestaurantDiscountDTO restaurantDiscountDTO = restaurantDiscountMapper.toDto(restaurantDiscount);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RestaurantDiscount> restaurantDiscountList = restaurantDiscountRepository.findAll().collectList().block();
        assertThat(restaurantDiscountList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPercentageIsRequired() throws Exception {
        int databaseSizeBeforeTest = restaurantDiscountRepository.findAll().collectList().block().size();
        // set the field null
        restaurantDiscount.setPercentage(null);

        // Create the RestaurantDiscount, which fails.
        RestaurantDiscountDTO restaurantDiscountDTO = restaurantDiscountMapper.toDto(restaurantDiscount);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RestaurantDiscount> restaurantDiscountList = restaurantDiscountRepository.findAll().collectList().block();
        assertThat(restaurantDiscountList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRestaurantDiscountsAsStream() {
        // Initialize the database
        restaurantDiscount.setId(UUID.randomUUID().toString());
        restaurantDiscountRepository.save(restaurantDiscount).block();

        List<RestaurantDiscount> restaurantDiscountList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(RestaurantDiscountDTO.class)
            .getResponseBody()
            .map(restaurantDiscountMapper::toEntity)
            .filter(restaurantDiscount::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(restaurantDiscountList).isNotNull();
        assertThat(restaurantDiscountList).hasSize(1);
        RestaurantDiscount testRestaurantDiscount = restaurantDiscountList.get(0);
        assertThat(testRestaurantDiscount.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testRestaurantDiscount.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRestaurantDiscount.getPercentage()).isEqualTo(DEFAULT_PERCENTAGE);
    }

    @Test
    void getAllRestaurantDiscounts() {
        // Initialize the database
        restaurantDiscount.setId(UUID.randomUUID().toString());
        restaurantDiscountRepository.save(restaurantDiscount).block();

        // Get all the restaurantDiscountList
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
            .value(hasItem(restaurantDiscount.getId()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].percentage")
            .value(hasItem(DEFAULT_PERCENTAGE.doubleValue()));
    }

    @Test
    void getRestaurantDiscount() {
        // Initialize the database
        restaurantDiscount.setId(UUID.randomUUID().toString());
        restaurantDiscountRepository.save(restaurantDiscount).block();

        // Get the restaurantDiscount
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, restaurantDiscount.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(restaurantDiscount.getId()))
            .jsonPath("$.code")
            .value(is(DEFAULT_CODE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.percentage")
            .value(is(DEFAULT_PERCENTAGE.doubleValue()));
    }

    @Test
    void getNonExistingRestaurantDiscount() {
        // Get the restaurantDiscount
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRestaurantDiscount() throws Exception {
        // Configure the mock search repository
        when(mockRestaurantDiscountSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        restaurantDiscount.setId(UUID.randomUUID().toString());
        restaurantDiscountRepository.save(restaurantDiscount).block();

        int databaseSizeBeforeUpdate = restaurantDiscountRepository.findAll().collectList().block().size();

        // Update the restaurantDiscount
        RestaurantDiscount updatedRestaurantDiscount = restaurantDiscountRepository.findById(restaurantDiscount.getId()).block();
        updatedRestaurantDiscount.code(UPDATED_CODE).description(UPDATED_DESCRIPTION).percentage(UPDATED_PERCENTAGE);
        RestaurantDiscountDTO restaurantDiscountDTO = restaurantDiscountMapper.toDto(updatedRestaurantDiscount);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, restaurantDiscountDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDiscountDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RestaurantDiscount in the database
        List<RestaurantDiscount> restaurantDiscountList = restaurantDiscountRepository.findAll().collectList().block();
        assertThat(restaurantDiscountList).hasSize(databaseSizeBeforeUpdate);
        RestaurantDiscount testRestaurantDiscount = restaurantDiscountList.get(restaurantDiscountList.size() - 1);
        assertThat(testRestaurantDiscount.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testRestaurantDiscount.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRestaurantDiscount.getPercentage()).isEqualTo(UPDATED_PERCENTAGE);

        // Validate the RestaurantDiscount in Elasticsearch
        verify(mockRestaurantDiscountSearchRepository).save(testRestaurantDiscount);
    }

    @Test
    void putNonExistingRestaurantDiscount() throws Exception {
        int databaseSizeBeforeUpdate = restaurantDiscountRepository.findAll().collectList().block().size();
        restaurantDiscount.setId(UUID.randomUUID().toString());

        // Create the RestaurantDiscount
        RestaurantDiscountDTO restaurantDiscountDTO = restaurantDiscountMapper.toDto(restaurantDiscount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, restaurantDiscountDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RestaurantDiscount in the database
        List<RestaurantDiscount> restaurantDiscountList = restaurantDiscountRepository.findAll().collectList().block();
        assertThat(restaurantDiscountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RestaurantDiscount in Elasticsearch
        verify(mockRestaurantDiscountSearchRepository, times(0)).save(restaurantDiscount);
    }

    @Test
    void putWithIdMismatchRestaurantDiscount() throws Exception {
        int databaseSizeBeforeUpdate = restaurantDiscountRepository.findAll().collectList().block().size();
        restaurantDiscount.setId(UUID.randomUUID().toString());

        // Create the RestaurantDiscount
        RestaurantDiscountDTO restaurantDiscountDTO = restaurantDiscountMapper.toDto(restaurantDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RestaurantDiscount in the database
        List<RestaurantDiscount> restaurantDiscountList = restaurantDiscountRepository.findAll().collectList().block();
        assertThat(restaurantDiscountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RestaurantDiscount in Elasticsearch
        verify(mockRestaurantDiscountSearchRepository, times(0)).save(restaurantDiscount);
    }

    @Test
    void putWithMissingIdPathParamRestaurantDiscount() throws Exception {
        int databaseSizeBeforeUpdate = restaurantDiscountRepository.findAll().collectList().block().size();
        restaurantDiscount.setId(UUID.randomUUID().toString());

        // Create the RestaurantDiscount
        RestaurantDiscountDTO restaurantDiscountDTO = restaurantDiscountMapper.toDto(restaurantDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDiscountDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RestaurantDiscount in the database
        List<RestaurantDiscount> restaurantDiscountList = restaurantDiscountRepository.findAll().collectList().block();
        assertThat(restaurantDiscountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RestaurantDiscount in Elasticsearch
        verify(mockRestaurantDiscountSearchRepository, times(0)).save(restaurantDiscount);
    }

    @Test
    void partialUpdateRestaurantDiscountWithPatch() throws Exception {
        // Initialize the database
        restaurantDiscount.setId(UUID.randomUUID().toString());
        restaurantDiscountRepository.save(restaurantDiscount).block();

        int databaseSizeBeforeUpdate = restaurantDiscountRepository.findAll().collectList().block().size();

        // Update the restaurantDiscount using partial update
        RestaurantDiscount partialUpdatedRestaurantDiscount = new RestaurantDiscount();
        partialUpdatedRestaurantDiscount.setId(restaurantDiscount.getId());

        partialUpdatedRestaurantDiscount.code(UPDATED_CODE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRestaurantDiscount.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRestaurantDiscount))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RestaurantDiscount in the database
        List<RestaurantDiscount> restaurantDiscountList = restaurantDiscountRepository.findAll().collectList().block();
        assertThat(restaurantDiscountList).hasSize(databaseSizeBeforeUpdate);
        RestaurantDiscount testRestaurantDiscount = restaurantDiscountList.get(restaurantDiscountList.size() - 1);
        assertThat(testRestaurantDiscount.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testRestaurantDiscount.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRestaurantDiscount.getPercentage()).isEqualTo(DEFAULT_PERCENTAGE);
    }

    @Test
    void fullUpdateRestaurantDiscountWithPatch() throws Exception {
        // Initialize the database
        restaurantDiscount.setId(UUID.randomUUID().toString());
        restaurantDiscountRepository.save(restaurantDiscount).block();

        int databaseSizeBeforeUpdate = restaurantDiscountRepository.findAll().collectList().block().size();

        // Update the restaurantDiscount using partial update
        RestaurantDiscount partialUpdatedRestaurantDiscount = new RestaurantDiscount();
        partialUpdatedRestaurantDiscount.setId(restaurantDiscount.getId());

        partialUpdatedRestaurantDiscount.code(UPDATED_CODE).description(UPDATED_DESCRIPTION).percentage(UPDATED_PERCENTAGE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRestaurantDiscount.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRestaurantDiscount))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RestaurantDiscount in the database
        List<RestaurantDiscount> restaurantDiscountList = restaurantDiscountRepository.findAll().collectList().block();
        assertThat(restaurantDiscountList).hasSize(databaseSizeBeforeUpdate);
        RestaurantDiscount testRestaurantDiscount = restaurantDiscountList.get(restaurantDiscountList.size() - 1);
        assertThat(testRestaurantDiscount.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testRestaurantDiscount.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRestaurantDiscount.getPercentage()).isEqualTo(UPDATED_PERCENTAGE);
    }

    @Test
    void patchNonExistingRestaurantDiscount() throws Exception {
        int databaseSizeBeforeUpdate = restaurantDiscountRepository.findAll().collectList().block().size();
        restaurantDiscount.setId(UUID.randomUUID().toString());

        // Create the RestaurantDiscount
        RestaurantDiscountDTO restaurantDiscountDTO = restaurantDiscountMapper.toDto(restaurantDiscount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, restaurantDiscountDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RestaurantDiscount in the database
        List<RestaurantDiscount> restaurantDiscountList = restaurantDiscountRepository.findAll().collectList().block();
        assertThat(restaurantDiscountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RestaurantDiscount in Elasticsearch
        verify(mockRestaurantDiscountSearchRepository, times(0)).save(restaurantDiscount);
    }

    @Test
    void patchWithIdMismatchRestaurantDiscount() throws Exception {
        int databaseSizeBeforeUpdate = restaurantDiscountRepository.findAll().collectList().block().size();
        restaurantDiscount.setId(UUID.randomUUID().toString());

        // Create the RestaurantDiscount
        RestaurantDiscountDTO restaurantDiscountDTO = restaurantDiscountMapper.toDto(restaurantDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDiscountDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RestaurantDiscount in the database
        List<RestaurantDiscount> restaurantDiscountList = restaurantDiscountRepository.findAll().collectList().block();
        assertThat(restaurantDiscountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RestaurantDiscount in Elasticsearch
        verify(mockRestaurantDiscountSearchRepository, times(0)).save(restaurantDiscount);
    }

    @Test
    void patchWithMissingIdPathParamRestaurantDiscount() throws Exception {
        int databaseSizeBeforeUpdate = restaurantDiscountRepository.findAll().collectList().block().size();
        restaurantDiscount.setId(UUID.randomUUID().toString());

        // Create the RestaurantDiscount
        RestaurantDiscountDTO restaurantDiscountDTO = restaurantDiscountMapper.toDto(restaurantDiscount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDiscountDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RestaurantDiscount in the database
        List<RestaurantDiscount> restaurantDiscountList = restaurantDiscountRepository.findAll().collectList().block();
        assertThat(restaurantDiscountList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RestaurantDiscount in Elasticsearch
        verify(mockRestaurantDiscountSearchRepository, times(0)).save(restaurantDiscount);
    }

    @Test
    void deleteRestaurantDiscount() {
        // Configure the mock search repository
        when(mockRestaurantDiscountSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockRestaurantDiscountSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        restaurantDiscount.setId(UUID.randomUUID().toString());
        restaurantDiscountRepository.save(restaurantDiscount).block();

        int databaseSizeBeforeDelete = restaurantDiscountRepository.findAll().collectList().block().size();

        // Delete the restaurantDiscount
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, restaurantDiscount.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RestaurantDiscount> restaurantDiscountList = restaurantDiscountRepository.findAll().collectList().block();
        assertThat(restaurantDiscountList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the RestaurantDiscount in Elasticsearch
        verify(mockRestaurantDiscountSearchRepository, times(1)).deleteById(restaurantDiscount.getId());
    }

    @Test
    void searchRestaurantDiscount() {
        // Configure the mock search repository
        when(mockRestaurantDiscountSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        restaurantDiscount.setId(UUID.randomUUID().toString());
        restaurantDiscountRepository.save(restaurantDiscount).block();
        when(mockRestaurantDiscountSearchRepository.search("id:" + restaurantDiscount.getId())).thenReturn(Flux.just(restaurantDiscount));

        // Search the restaurantDiscount
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + restaurantDiscount.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(restaurantDiscount.getId()))
            .jsonPath("$.[*].code")
            .value(hasItem(DEFAULT_CODE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].percentage")
            .value(hasItem(DEFAULT_PERCENTAGE.doubleValue()));
    }
}
