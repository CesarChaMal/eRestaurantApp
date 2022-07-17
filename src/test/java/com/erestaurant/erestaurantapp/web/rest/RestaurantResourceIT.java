package com.erestaurant.erestaurantapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.erestaurantapp.IntegrationTest;
import com.erestaurant.erestaurantapp.domain.Restaurant;
import com.erestaurant.erestaurantapp.repository.EntityManager;
import com.erestaurant.erestaurantapp.repository.RestaurantRepository;
import com.erestaurant.erestaurantapp.repository.search.RestaurantSearchRepository;
import com.erestaurant.erestaurantapp.service.dto.RestaurantDTO;
import com.erestaurant.erestaurantapp.service.mapper.RestaurantMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link RestaurantResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RestaurantResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Float DEFAULT_RATING = 1F;
    private static final Float UPDATED_RATING = 2F;

    private static final String ENTITY_API_URL = "/api/restaurants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/restaurants";

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantMapper restaurantMapper;

    /**
     * This repository is mocked in the com.erestaurant.erestaurantapp.repository.search test package.
     *
     * @see com.erestaurant.erestaurantapp.repository.search.RestaurantSearchRepositoryMockConfiguration
     */
    @Autowired
    private RestaurantSearchRepository mockRestaurantSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Restaurant restaurant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Restaurant createEntity(EntityManager em) {
        Restaurant restaurant = new Restaurant()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .email(DEFAULT_EMAIL)
            .rating(DEFAULT_RATING);
        return restaurant;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Restaurant createUpdatedEntity(EntityManager em) {
        Restaurant restaurant = new Restaurant()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .email(UPDATED_EMAIL)
            .rating(UPDATED_RATING);
        return restaurant;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Restaurant.class).block();
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
        restaurant = createEntity(em);
    }

    @Test
    void createRestaurant() throws Exception {
        int databaseSizeBeforeCreate = restaurantRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockRestaurantSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll().collectList().block();
        assertThat(restaurantList).hasSize(databaseSizeBeforeCreate + 1);
        Restaurant testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRestaurant.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRestaurant.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testRestaurant.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testRestaurant.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testRestaurant.getRating()).isEqualTo(DEFAULT_RATING);

        // Validate the Restaurant in Elasticsearch
        verify(mockRestaurantSearchRepository, times(1)).save(testRestaurant);
    }

    @Test
    void createRestaurantWithExistingId() throws Exception {
        // Create the Restaurant with an existing ID
        restaurant.setId("existing_id");
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        int databaseSizeBeforeCreate = restaurantRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll().collectList().block();
        assertThat(restaurantList).hasSize(databaseSizeBeforeCreate);

        // Validate the Restaurant in Elasticsearch
        verify(mockRestaurantSearchRepository, times(0)).save(restaurant);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = restaurantRepository.findAll().collectList().block().size();
        // set the field null
        restaurant.setName(null);

        // Create the Restaurant, which fails.
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Restaurant> restaurantList = restaurantRepository.findAll().collectList().block();
        assertThat(restaurantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkRatingIsRequired() throws Exception {
        int databaseSizeBeforeTest = restaurantRepository.findAll().collectList().block().size();
        // set the field null
        restaurant.setRating(null);

        // Create the Restaurant, which fails.
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Restaurant> restaurantList = restaurantRepository.findAll().collectList().block();
        assertThat(restaurantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRestaurants() {
        // Initialize the database
        restaurant.setId(UUID.randomUUID().toString());
        restaurantRepository.save(restaurant).block();

        // Get all the restaurantList
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
            .value(hasItem(restaurant.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].imageContentType")
            .value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.[*].image")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING.doubleValue()));
    }

    @Test
    void getRestaurant() {
        // Initialize the database
        restaurant.setId(UUID.randomUUID().toString());
        restaurantRepository.save(restaurant).block();

        // Get the restaurant
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, restaurant.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(restaurant.getId()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.imageContentType")
            .value(is(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.image")
            .value(is(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.rating")
            .value(is(DEFAULT_RATING.doubleValue()));
    }

    @Test
    void getNonExistingRestaurant() {
        // Get the restaurant
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRestaurant() throws Exception {
        // Configure the mock search repository
        when(mockRestaurantSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        restaurant.setId(UUID.randomUUID().toString());
        restaurantRepository.save(restaurant).block();

        int databaseSizeBeforeUpdate = restaurantRepository.findAll().collectList().block().size();

        // Update the restaurant
        Restaurant updatedRestaurant = restaurantRepository.findById(restaurant.getId()).block();
        updatedRestaurant
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .email(UPDATED_EMAIL)
            .rating(UPDATED_RATING);
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(updatedRestaurant);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, restaurantDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll().collectList().block();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
        Restaurant testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRestaurant.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRestaurant.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testRestaurant.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testRestaurant.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testRestaurant.getRating()).isEqualTo(UPDATED_RATING);

        // Validate the Restaurant in Elasticsearch
        verify(mockRestaurantSearchRepository).save(testRestaurant);
    }

    @Test
    void putNonExistingRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().collectList().block().size();
        restaurant.setId(UUID.randomUUID().toString());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, restaurantDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll().collectList().block();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Restaurant in Elasticsearch
        verify(mockRestaurantSearchRepository, times(0)).save(restaurant);
    }

    @Test
    void putWithIdMismatchRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().collectList().block().size();
        restaurant.setId(UUID.randomUUID().toString());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll().collectList().block();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Restaurant in Elasticsearch
        verify(mockRestaurantSearchRepository, times(0)).save(restaurant);
    }

    @Test
    void putWithMissingIdPathParamRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().collectList().block().size();
        restaurant.setId(UUID.randomUUID().toString());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll().collectList().block();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Restaurant in Elasticsearch
        verify(mockRestaurantSearchRepository, times(0)).save(restaurant);
    }

    @Test
    void partialUpdateRestaurantWithPatch() throws Exception {
        // Initialize the database
        restaurant.setId(UUID.randomUUID().toString());
        restaurantRepository.save(restaurant).block();

        int databaseSizeBeforeUpdate = restaurantRepository.findAll().collectList().block().size();

        // Update the restaurant using partial update
        Restaurant partialUpdatedRestaurant = new Restaurant();
        partialUpdatedRestaurant.setId(restaurant.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRestaurant.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRestaurant))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll().collectList().block();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
        Restaurant testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRestaurant.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRestaurant.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testRestaurant.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testRestaurant.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testRestaurant.getRating()).isEqualTo(DEFAULT_RATING);
    }

    @Test
    void fullUpdateRestaurantWithPatch() throws Exception {
        // Initialize the database
        restaurant.setId(UUID.randomUUID().toString());
        restaurantRepository.save(restaurant).block();

        int databaseSizeBeforeUpdate = restaurantRepository.findAll().collectList().block().size();

        // Update the restaurant using partial update
        Restaurant partialUpdatedRestaurant = new Restaurant();
        partialUpdatedRestaurant.setId(restaurant.getId());

        partialUpdatedRestaurant
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .email(UPDATED_EMAIL)
            .rating(UPDATED_RATING);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRestaurant.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRestaurant))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll().collectList().block();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
        Restaurant testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRestaurant.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRestaurant.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testRestaurant.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testRestaurant.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testRestaurant.getRating()).isEqualTo(UPDATED_RATING);
    }

    @Test
    void patchNonExistingRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().collectList().block().size();
        restaurant.setId(UUID.randomUUID().toString());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, restaurantDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll().collectList().block();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Restaurant in Elasticsearch
        verify(mockRestaurantSearchRepository, times(0)).save(restaurant);
    }

    @Test
    void patchWithIdMismatchRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().collectList().block().size();
        restaurant.setId(UUID.randomUUID().toString());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll().collectList().block();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Restaurant in Elasticsearch
        verify(mockRestaurantSearchRepository, times(0)).save(restaurant);
    }

    @Test
    void patchWithMissingIdPathParamRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().collectList().block().size();
        restaurant.setId(UUID.randomUUID().toString());

        // Create the Restaurant
        RestaurantDTO restaurantDTO = restaurantMapper.toDto(restaurant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll().collectList().block();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Restaurant in Elasticsearch
        verify(mockRestaurantSearchRepository, times(0)).save(restaurant);
    }

    @Test
    void deleteRestaurant() {
        // Configure the mock search repository
        when(mockRestaurantSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockRestaurantSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        restaurant.setId(UUID.randomUUID().toString());
        restaurantRepository.save(restaurant).block();

        int databaseSizeBeforeDelete = restaurantRepository.findAll().collectList().block().size();

        // Delete the restaurant
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, restaurant.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Restaurant> restaurantList = restaurantRepository.findAll().collectList().block();
        assertThat(restaurantList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Restaurant in Elasticsearch
        verify(mockRestaurantSearchRepository, times(1)).deleteById(restaurant.getId());
    }

    @Test
    void searchRestaurant() {
        // Configure the mock search repository
        when(mockRestaurantSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockRestaurantSearchRepository.count()).thenReturn(Mono.just(1L));
        // Initialize the database
        restaurant.setId(UUID.randomUUID().toString());
        restaurantRepository.save(restaurant).block();
        when(mockRestaurantSearchRepository.search("id:" + restaurant.getId(), PageRequest.of(0, 20))).thenReturn(Flux.just(restaurant));

        // Search the restaurant
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + restaurant.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(restaurant.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].imageContentType")
            .value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.[*].image")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING.doubleValue()));
    }
}
