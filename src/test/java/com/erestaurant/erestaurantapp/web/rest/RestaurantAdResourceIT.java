package com.erestaurant.erestaurantapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.erestaurantapp.IntegrationTest;
import com.erestaurant.erestaurantapp.domain.RestaurantAd;
import com.erestaurant.erestaurantapp.repository.EntityManager;
import com.erestaurant.erestaurantapp.repository.RestaurantAdRepository;
import com.erestaurant.erestaurantapp.repository.search.RestaurantAdSearchRepository;
import com.erestaurant.erestaurantapp.service.dto.RestaurantAdDTO;
import com.erestaurant.erestaurantapp.service.mapper.RestaurantAdMapper;
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
 * Integration tests for the {@link RestaurantAdResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RestaurantAdResourceIT {

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/restaurant-ads";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/restaurant-ads";

    @Autowired
    private RestaurantAdRepository restaurantAdRepository;

    @Autowired
    private RestaurantAdMapper restaurantAdMapper;

    /**
     * This repository is mocked in the com.erestaurant.erestaurantapp.repository.search test package.
     *
     * @see com.erestaurant.erestaurantapp.repository.search.RestaurantAdSearchRepositoryMockConfiguration
     */
    @Autowired
    private RestaurantAdSearchRepository mockRestaurantAdSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RestaurantAd restaurantAd;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RestaurantAd createEntity(EntityManager em) {
        RestaurantAd restaurantAd = new RestaurantAd().url(DEFAULT_URL).description(DEFAULT_DESCRIPTION);
        return restaurantAd;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RestaurantAd createUpdatedEntity(EntityManager em) {
        RestaurantAd restaurantAd = new RestaurantAd().url(UPDATED_URL).description(UPDATED_DESCRIPTION);
        return restaurantAd;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RestaurantAd.class).block();
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
        restaurantAd = createEntity(em);
    }

    @Test
    void createRestaurantAd() throws Exception {
        int databaseSizeBeforeCreate = restaurantAdRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockRestaurantAdSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the RestaurantAd
        RestaurantAdDTO restaurantAdDTO = restaurantAdMapper.toDto(restaurantAd);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantAdDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RestaurantAd in the database
        List<RestaurantAd> restaurantAdList = restaurantAdRepository.findAll().collectList().block();
        assertThat(restaurantAdList).hasSize(databaseSizeBeforeCreate + 1);
        RestaurantAd testRestaurantAd = restaurantAdList.get(restaurantAdList.size() - 1);
        assertThat(testRestaurantAd.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testRestaurantAd.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the RestaurantAd in Elasticsearch
        verify(mockRestaurantAdSearchRepository, times(1)).save(testRestaurantAd);
    }

    @Test
    void createRestaurantAdWithExistingId() throws Exception {
        // Create the RestaurantAd with an existing ID
        restaurantAd.setId("existing_id");
        RestaurantAdDTO restaurantAdDTO = restaurantAdMapper.toDto(restaurantAd);

        int databaseSizeBeforeCreate = restaurantAdRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantAdDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RestaurantAd in the database
        List<RestaurantAd> restaurantAdList = restaurantAdRepository.findAll().collectList().block();
        assertThat(restaurantAdList).hasSize(databaseSizeBeforeCreate);

        // Validate the RestaurantAd in Elasticsearch
        verify(mockRestaurantAdSearchRepository, times(0)).save(restaurantAd);
    }

    @Test
    void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = restaurantAdRepository.findAll().collectList().block().size();
        // set the field null
        restaurantAd.setUrl(null);

        // Create the RestaurantAd, which fails.
        RestaurantAdDTO restaurantAdDTO = restaurantAdMapper.toDto(restaurantAd);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantAdDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RestaurantAd> restaurantAdList = restaurantAdRepository.findAll().collectList().block();
        assertThat(restaurantAdList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRestaurantAdsAsStream() {
        // Initialize the database
        restaurantAd.setId(UUID.randomUUID().toString());
        restaurantAdRepository.save(restaurantAd).block();

        List<RestaurantAd> restaurantAdList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(RestaurantAdDTO.class)
            .getResponseBody()
            .map(restaurantAdMapper::toEntity)
            .filter(restaurantAd::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(restaurantAdList).isNotNull();
        assertThat(restaurantAdList).hasSize(1);
        RestaurantAd testRestaurantAd = restaurantAdList.get(0);
        assertThat(testRestaurantAd.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testRestaurantAd.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllRestaurantAds() {
        // Initialize the database
        restaurantAd.setId(UUID.randomUUID().toString());
        restaurantAdRepository.save(restaurantAd).block();

        // Get all the restaurantAdList
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
            .value(hasItem(restaurantAd.getId()))
            .jsonPath("$.[*].url")
            .value(hasItem(DEFAULT_URL))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getRestaurantAd() {
        // Initialize the database
        restaurantAd.setId(UUID.randomUUID().toString());
        restaurantAdRepository.save(restaurantAd).block();

        // Get the restaurantAd
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, restaurantAd.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(restaurantAd.getId()))
            .jsonPath("$.url")
            .value(is(DEFAULT_URL))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getNonExistingRestaurantAd() {
        // Get the restaurantAd
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRestaurantAd() throws Exception {
        // Configure the mock search repository
        when(mockRestaurantAdSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        restaurantAd.setId(UUID.randomUUID().toString());
        restaurantAdRepository.save(restaurantAd).block();

        int databaseSizeBeforeUpdate = restaurantAdRepository.findAll().collectList().block().size();

        // Update the restaurantAd
        RestaurantAd updatedRestaurantAd = restaurantAdRepository.findById(restaurantAd.getId()).block();
        updatedRestaurantAd.url(UPDATED_URL).description(UPDATED_DESCRIPTION);
        RestaurantAdDTO restaurantAdDTO = restaurantAdMapper.toDto(updatedRestaurantAd);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, restaurantAdDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantAdDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RestaurantAd in the database
        List<RestaurantAd> restaurantAdList = restaurantAdRepository.findAll().collectList().block();
        assertThat(restaurantAdList).hasSize(databaseSizeBeforeUpdate);
        RestaurantAd testRestaurantAd = restaurantAdList.get(restaurantAdList.size() - 1);
        assertThat(testRestaurantAd.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testRestaurantAd.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the RestaurantAd in Elasticsearch
        verify(mockRestaurantAdSearchRepository).save(testRestaurantAd);
    }

    @Test
    void putNonExistingRestaurantAd() throws Exception {
        int databaseSizeBeforeUpdate = restaurantAdRepository.findAll().collectList().block().size();
        restaurantAd.setId(UUID.randomUUID().toString());

        // Create the RestaurantAd
        RestaurantAdDTO restaurantAdDTO = restaurantAdMapper.toDto(restaurantAd);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, restaurantAdDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantAdDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RestaurantAd in the database
        List<RestaurantAd> restaurantAdList = restaurantAdRepository.findAll().collectList().block();
        assertThat(restaurantAdList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RestaurantAd in Elasticsearch
        verify(mockRestaurantAdSearchRepository, times(0)).save(restaurantAd);
    }

    @Test
    void putWithIdMismatchRestaurantAd() throws Exception {
        int databaseSizeBeforeUpdate = restaurantAdRepository.findAll().collectList().block().size();
        restaurantAd.setId(UUID.randomUUID().toString());

        // Create the RestaurantAd
        RestaurantAdDTO restaurantAdDTO = restaurantAdMapper.toDto(restaurantAd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantAdDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RestaurantAd in the database
        List<RestaurantAd> restaurantAdList = restaurantAdRepository.findAll().collectList().block();
        assertThat(restaurantAdList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RestaurantAd in Elasticsearch
        verify(mockRestaurantAdSearchRepository, times(0)).save(restaurantAd);
    }

    @Test
    void putWithMissingIdPathParamRestaurantAd() throws Exception {
        int databaseSizeBeforeUpdate = restaurantAdRepository.findAll().collectList().block().size();
        restaurantAd.setId(UUID.randomUUID().toString());

        // Create the RestaurantAd
        RestaurantAdDTO restaurantAdDTO = restaurantAdMapper.toDto(restaurantAd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantAdDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RestaurantAd in the database
        List<RestaurantAd> restaurantAdList = restaurantAdRepository.findAll().collectList().block();
        assertThat(restaurantAdList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RestaurantAd in Elasticsearch
        verify(mockRestaurantAdSearchRepository, times(0)).save(restaurantAd);
    }

    @Test
    void partialUpdateRestaurantAdWithPatch() throws Exception {
        // Initialize the database
        restaurantAd.setId(UUID.randomUUID().toString());
        restaurantAdRepository.save(restaurantAd).block();

        int databaseSizeBeforeUpdate = restaurantAdRepository.findAll().collectList().block().size();

        // Update the restaurantAd using partial update
        RestaurantAd partialUpdatedRestaurantAd = new RestaurantAd();
        partialUpdatedRestaurantAd.setId(restaurantAd.getId());

        partialUpdatedRestaurantAd.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRestaurantAd.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRestaurantAd))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RestaurantAd in the database
        List<RestaurantAd> restaurantAdList = restaurantAdRepository.findAll().collectList().block();
        assertThat(restaurantAdList).hasSize(databaseSizeBeforeUpdate);
        RestaurantAd testRestaurantAd = restaurantAdList.get(restaurantAdList.size() - 1);
        assertThat(testRestaurantAd.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testRestaurantAd.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void fullUpdateRestaurantAdWithPatch() throws Exception {
        // Initialize the database
        restaurantAd.setId(UUID.randomUUID().toString());
        restaurantAdRepository.save(restaurantAd).block();

        int databaseSizeBeforeUpdate = restaurantAdRepository.findAll().collectList().block().size();

        // Update the restaurantAd using partial update
        RestaurantAd partialUpdatedRestaurantAd = new RestaurantAd();
        partialUpdatedRestaurantAd.setId(restaurantAd.getId());

        partialUpdatedRestaurantAd.url(UPDATED_URL).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRestaurantAd.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRestaurantAd))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RestaurantAd in the database
        List<RestaurantAd> restaurantAdList = restaurantAdRepository.findAll().collectList().block();
        assertThat(restaurantAdList).hasSize(databaseSizeBeforeUpdate);
        RestaurantAd testRestaurantAd = restaurantAdList.get(restaurantAdList.size() - 1);
        assertThat(testRestaurantAd.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testRestaurantAd.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingRestaurantAd() throws Exception {
        int databaseSizeBeforeUpdate = restaurantAdRepository.findAll().collectList().block().size();
        restaurantAd.setId(UUID.randomUUID().toString());

        // Create the RestaurantAd
        RestaurantAdDTO restaurantAdDTO = restaurantAdMapper.toDto(restaurantAd);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, restaurantAdDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantAdDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RestaurantAd in the database
        List<RestaurantAd> restaurantAdList = restaurantAdRepository.findAll().collectList().block();
        assertThat(restaurantAdList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RestaurantAd in Elasticsearch
        verify(mockRestaurantAdSearchRepository, times(0)).save(restaurantAd);
    }

    @Test
    void patchWithIdMismatchRestaurantAd() throws Exception {
        int databaseSizeBeforeUpdate = restaurantAdRepository.findAll().collectList().block().size();
        restaurantAd.setId(UUID.randomUUID().toString());

        // Create the RestaurantAd
        RestaurantAdDTO restaurantAdDTO = restaurantAdMapper.toDto(restaurantAd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantAdDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RestaurantAd in the database
        List<RestaurantAd> restaurantAdList = restaurantAdRepository.findAll().collectList().block();
        assertThat(restaurantAdList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RestaurantAd in Elasticsearch
        verify(mockRestaurantAdSearchRepository, times(0)).save(restaurantAd);
    }

    @Test
    void patchWithMissingIdPathParamRestaurantAd() throws Exception {
        int databaseSizeBeforeUpdate = restaurantAdRepository.findAll().collectList().block().size();
        restaurantAd.setId(UUID.randomUUID().toString());

        // Create the RestaurantAd
        RestaurantAdDTO restaurantAdDTO = restaurantAdMapper.toDto(restaurantAd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(restaurantAdDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RestaurantAd in the database
        List<RestaurantAd> restaurantAdList = restaurantAdRepository.findAll().collectList().block();
        assertThat(restaurantAdList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RestaurantAd in Elasticsearch
        verify(mockRestaurantAdSearchRepository, times(0)).save(restaurantAd);
    }

    @Test
    void deleteRestaurantAd() {
        // Configure the mock search repository
        when(mockRestaurantAdSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockRestaurantAdSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        restaurantAd.setId(UUID.randomUUID().toString());
        restaurantAdRepository.save(restaurantAd).block();

        int databaseSizeBeforeDelete = restaurantAdRepository.findAll().collectList().block().size();

        // Delete the restaurantAd
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, restaurantAd.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RestaurantAd> restaurantAdList = restaurantAdRepository.findAll().collectList().block();
        assertThat(restaurantAdList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the RestaurantAd in Elasticsearch
        verify(mockRestaurantAdSearchRepository, times(1)).deleteById(restaurantAd.getId());
    }

    @Test
    void searchRestaurantAd() {
        // Configure the mock search repository
        when(mockRestaurantAdSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        restaurantAd.setId(UUID.randomUUID().toString());
        restaurantAdRepository.save(restaurantAd).block();
        when(mockRestaurantAdSearchRepository.search("id:" + restaurantAd.getId())).thenReturn(Flux.just(restaurantAd));

        // Search the restaurantAd
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + restaurantAd.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(restaurantAd.getId()))
            .jsonPath("$.[*].url")
            .value(hasItem(DEFAULT_URL))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }
}
