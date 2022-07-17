package com.erestaurant.erestaurantapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.erestaurantapp.IntegrationTest;
import com.erestaurant.erestaurantapp.domain.AppAd;
import com.erestaurant.erestaurantapp.repository.AppAdRepository;
import com.erestaurant.erestaurantapp.repository.EntityManager;
import com.erestaurant.erestaurantapp.repository.search.AppAdSearchRepository;
import com.erestaurant.erestaurantapp.service.dto.AppAdDTO;
import com.erestaurant.erestaurantapp.service.mapper.AppAdMapper;
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
 * Integration tests for the {@link AppAdResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AppAdResourceIT {

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/app-ads";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/app-ads";

    @Autowired
    private AppAdRepository appAdRepository;

    @Autowired
    private AppAdMapper appAdMapper;

    /**
     * This repository is mocked in the com.erestaurant.erestaurantapp.repository.search test package.
     *
     * @see com.erestaurant.erestaurantapp.repository.search.AppAdSearchRepositoryMockConfiguration
     */
    @Autowired
    private AppAdSearchRepository mockAppAdSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private AppAd appAd;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppAd createEntity(EntityManager em) {
        AppAd appAd = new AppAd().url(DEFAULT_URL).description(DEFAULT_DESCRIPTION);
        return appAd;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppAd createUpdatedEntity(EntityManager em) {
        AppAd appAd = new AppAd().url(UPDATED_URL).description(UPDATED_DESCRIPTION);
        return appAd;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(AppAd.class).block();
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
        appAd = createEntity(em);
    }

    @Test
    void createAppAd() throws Exception {
        int databaseSizeBeforeCreate = appAdRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockAppAdSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the AppAd
        AppAdDTO appAdDTO = appAdMapper.toDto(appAd);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appAdDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the AppAd in the database
        List<AppAd> appAdList = appAdRepository.findAll().collectList().block();
        assertThat(appAdList).hasSize(databaseSizeBeforeCreate + 1);
        AppAd testAppAd = appAdList.get(appAdList.size() - 1);
        assertThat(testAppAd.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testAppAd.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the AppAd in Elasticsearch
        verify(mockAppAdSearchRepository, times(1)).save(testAppAd);
    }

    @Test
    void createAppAdWithExistingId() throws Exception {
        // Create the AppAd with an existing ID
        appAd.setId("existing_id");
        AppAdDTO appAdDTO = appAdMapper.toDto(appAd);

        int databaseSizeBeforeCreate = appAdRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appAdDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppAd in the database
        List<AppAd> appAdList = appAdRepository.findAll().collectList().block();
        assertThat(appAdList).hasSize(databaseSizeBeforeCreate);

        // Validate the AppAd in Elasticsearch
        verify(mockAppAdSearchRepository, times(0)).save(appAd);
    }

    @Test
    void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = appAdRepository.findAll().collectList().block().size();
        // set the field null
        appAd.setUrl(null);

        // Create the AppAd, which fails.
        AppAdDTO appAdDTO = appAdMapper.toDto(appAd);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appAdDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<AppAd> appAdList = appAdRepository.findAll().collectList().block();
        assertThat(appAdList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllAppAdsAsStream() {
        // Initialize the database
        appAd.setId(UUID.randomUUID().toString());
        appAdRepository.save(appAd).block();

        List<AppAd> appAdList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(AppAdDTO.class)
            .getResponseBody()
            .map(appAdMapper::toEntity)
            .filter(appAd::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(appAdList).isNotNull();
        assertThat(appAdList).hasSize(1);
        AppAd testAppAd = appAdList.get(0);
        assertThat(testAppAd.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testAppAd.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllAppAds() {
        // Initialize the database
        appAd.setId(UUID.randomUUID().toString());
        appAdRepository.save(appAd).block();

        // Get all the appAdList
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
            .value(hasItem(appAd.getId()))
            .jsonPath("$.[*].url")
            .value(hasItem(DEFAULT_URL))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getAppAd() {
        // Initialize the database
        appAd.setId(UUID.randomUUID().toString());
        appAdRepository.save(appAd).block();

        // Get the appAd
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, appAd.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(appAd.getId()))
            .jsonPath("$.url")
            .value(is(DEFAULT_URL))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getNonExistingAppAd() {
        // Get the appAd
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewAppAd() throws Exception {
        // Configure the mock search repository
        when(mockAppAdSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        appAd.setId(UUID.randomUUID().toString());
        appAdRepository.save(appAd).block();

        int databaseSizeBeforeUpdate = appAdRepository.findAll().collectList().block().size();

        // Update the appAd
        AppAd updatedAppAd = appAdRepository.findById(appAd.getId()).block();
        updatedAppAd.url(UPDATED_URL).description(UPDATED_DESCRIPTION);
        AppAdDTO appAdDTO = appAdMapper.toDto(updatedAppAd);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appAdDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appAdDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppAd in the database
        List<AppAd> appAdList = appAdRepository.findAll().collectList().block();
        assertThat(appAdList).hasSize(databaseSizeBeforeUpdate);
        AppAd testAppAd = appAdList.get(appAdList.size() - 1);
        assertThat(testAppAd.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testAppAd.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the AppAd in Elasticsearch
        verify(mockAppAdSearchRepository).save(testAppAd);
    }

    @Test
    void putNonExistingAppAd() throws Exception {
        int databaseSizeBeforeUpdate = appAdRepository.findAll().collectList().block().size();
        appAd.setId(UUID.randomUUID().toString());

        // Create the AppAd
        AppAdDTO appAdDTO = appAdMapper.toDto(appAd);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, appAdDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appAdDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppAd in the database
        List<AppAd> appAdList = appAdRepository.findAll().collectList().block();
        assertThat(appAdList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AppAd in Elasticsearch
        verify(mockAppAdSearchRepository, times(0)).save(appAd);
    }

    @Test
    void putWithIdMismatchAppAd() throws Exception {
        int databaseSizeBeforeUpdate = appAdRepository.findAll().collectList().block().size();
        appAd.setId(UUID.randomUUID().toString());

        // Create the AppAd
        AppAdDTO appAdDTO = appAdMapper.toDto(appAd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appAdDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppAd in the database
        List<AppAd> appAdList = appAdRepository.findAll().collectList().block();
        assertThat(appAdList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AppAd in Elasticsearch
        verify(mockAppAdSearchRepository, times(0)).save(appAd);
    }

    @Test
    void putWithMissingIdPathParamAppAd() throws Exception {
        int databaseSizeBeforeUpdate = appAdRepository.findAll().collectList().block().size();
        appAd.setId(UUID.randomUUID().toString());

        // Create the AppAd
        AppAdDTO appAdDTO = appAdMapper.toDto(appAd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(appAdDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppAd in the database
        List<AppAd> appAdList = appAdRepository.findAll().collectList().block();
        assertThat(appAdList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AppAd in Elasticsearch
        verify(mockAppAdSearchRepository, times(0)).save(appAd);
    }

    @Test
    void partialUpdateAppAdWithPatch() throws Exception {
        // Initialize the database
        appAd.setId(UUID.randomUUID().toString());
        appAdRepository.save(appAd).block();

        int databaseSizeBeforeUpdate = appAdRepository.findAll().collectList().block().size();

        // Update the appAd using partial update
        AppAd partialUpdatedAppAd = new AppAd();
        partialUpdatedAppAd.setId(appAd.getId());

        partialUpdatedAppAd.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppAd.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAppAd))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppAd in the database
        List<AppAd> appAdList = appAdRepository.findAll().collectList().block();
        assertThat(appAdList).hasSize(databaseSizeBeforeUpdate);
        AppAd testAppAd = appAdList.get(appAdList.size() - 1);
        assertThat(testAppAd.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testAppAd.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void fullUpdateAppAdWithPatch() throws Exception {
        // Initialize the database
        appAd.setId(UUID.randomUUID().toString());
        appAdRepository.save(appAd).block();

        int databaseSizeBeforeUpdate = appAdRepository.findAll().collectList().block().size();

        // Update the appAd using partial update
        AppAd partialUpdatedAppAd = new AppAd();
        partialUpdatedAppAd.setId(appAd.getId());

        partialUpdatedAppAd.url(UPDATED_URL).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAppAd.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAppAd))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AppAd in the database
        List<AppAd> appAdList = appAdRepository.findAll().collectList().block();
        assertThat(appAdList).hasSize(databaseSizeBeforeUpdate);
        AppAd testAppAd = appAdList.get(appAdList.size() - 1);
        assertThat(testAppAd.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testAppAd.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingAppAd() throws Exception {
        int databaseSizeBeforeUpdate = appAdRepository.findAll().collectList().block().size();
        appAd.setId(UUID.randomUUID().toString());

        // Create the AppAd
        AppAdDTO appAdDTO = appAdMapper.toDto(appAd);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, appAdDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(appAdDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppAd in the database
        List<AppAd> appAdList = appAdRepository.findAll().collectList().block();
        assertThat(appAdList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AppAd in Elasticsearch
        verify(mockAppAdSearchRepository, times(0)).save(appAd);
    }

    @Test
    void patchWithIdMismatchAppAd() throws Exception {
        int databaseSizeBeforeUpdate = appAdRepository.findAll().collectList().block().size();
        appAd.setId(UUID.randomUUID().toString());

        // Create the AppAd
        AppAdDTO appAdDTO = appAdMapper.toDto(appAd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(appAdDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AppAd in the database
        List<AppAd> appAdList = appAdRepository.findAll().collectList().block();
        assertThat(appAdList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AppAd in Elasticsearch
        verify(mockAppAdSearchRepository, times(0)).save(appAd);
    }

    @Test
    void patchWithMissingIdPathParamAppAd() throws Exception {
        int databaseSizeBeforeUpdate = appAdRepository.findAll().collectList().block().size();
        appAd.setId(UUID.randomUUID().toString());

        // Create the AppAd
        AppAdDTO appAdDTO = appAdMapper.toDto(appAd);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(appAdDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AppAd in the database
        List<AppAd> appAdList = appAdRepository.findAll().collectList().block();
        assertThat(appAdList).hasSize(databaseSizeBeforeUpdate);

        // Validate the AppAd in Elasticsearch
        verify(mockAppAdSearchRepository, times(0)).save(appAd);
    }

    @Test
    void deleteAppAd() {
        // Configure the mock search repository
        when(mockAppAdSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockAppAdSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        appAd.setId(UUID.randomUUID().toString());
        appAdRepository.save(appAd).block();

        int databaseSizeBeforeDelete = appAdRepository.findAll().collectList().block().size();

        // Delete the appAd
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, appAd.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<AppAd> appAdList = appAdRepository.findAll().collectList().block();
        assertThat(appAdList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the AppAd in Elasticsearch
        verify(mockAppAdSearchRepository, times(1)).deleteById(appAd.getId());
    }

    @Test
    void searchAppAd() {
        // Configure the mock search repository
        when(mockAppAdSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        appAd.setId(UUID.randomUUID().toString());
        appAdRepository.save(appAd).block();
        when(mockAppAdSearchRepository.search("id:" + appAd.getId())).thenReturn(Flux.just(appAd));

        // Search the appAd
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + appAd.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(appAd.getId()))
            .jsonPath("$.[*].url")
            .value(hasItem(DEFAULT_URL))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }
}
