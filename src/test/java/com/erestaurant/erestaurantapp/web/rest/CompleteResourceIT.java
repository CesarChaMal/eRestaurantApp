package com.erestaurant.erestaurantapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.erestaurantapp.IntegrationTest;
import com.erestaurant.erestaurantapp.domain.Complete;
import com.erestaurant.erestaurantapp.repository.CompleteRepository;
import com.erestaurant.erestaurantapp.repository.EntityManager;
import com.erestaurant.erestaurantapp.repository.search.CompleteSearchRepository;
import com.erestaurant.erestaurantapp.service.dto.CompleteDTO;
import com.erestaurant.erestaurantapp.service.mapper.CompleteMapper;
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
 * Integration tests for the {@link CompleteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CompleteResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ENABLED = false;
    private static final Boolean UPDATED_ENABLED = true;

    private static final String ENTITY_API_URL = "/api/completes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/completes";

    @Autowired
    private CompleteRepository completeRepository;

    @Autowired
    private CompleteMapper completeMapper;

    /**
     * This repository is mocked in the com.erestaurant.erestaurantapp.repository.search test package.
     *
     * @see com.erestaurant.erestaurantapp.repository.search.CompleteSearchRepositoryMockConfiguration
     */
    @Autowired
    private CompleteSearchRepository mockCompleteSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Complete complete;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Complete createEntity(EntityManager em) {
        Complete complete = new Complete().description(DEFAULT_DESCRIPTION).enabled(DEFAULT_ENABLED);
        return complete;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Complete createUpdatedEntity(EntityManager em) {
        Complete complete = new Complete().description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);
        return complete;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Complete.class).block();
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
        complete = createEntity(em);
    }

    @Test
    void createComplete() throws Exception {
        int databaseSizeBeforeCreate = completeRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockCompleteSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Complete
        CompleteDTO completeDTO = completeMapper.toDto(complete);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(completeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Complete in the database
        List<Complete> completeList = completeRepository.findAll().collectList().block();
        assertThat(completeList).hasSize(databaseSizeBeforeCreate + 1);
        Complete testComplete = completeList.get(completeList.size() - 1);
        assertThat(testComplete.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testComplete.getEnabled()).isEqualTo(DEFAULT_ENABLED);

        // Validate the Complete in Elasticsearch
        verify(mockCompleteSearchRepository, times(1)).save(testComplete);
    }

    @Test
    void createCompleteWithExistingId() throws Exception {
        // Create the Complete with an existing ID
        complete.setId("existing_id");
        CompleteDTO completeDTO = completeMapper.toDto(complete);

        int databaseSizeBeforeCreate = completeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(completeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Complete in the database
        List<Complete> completeList = completeRepository.findAll().collectList().block();
        assertThat(completeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Complete in Elasticsearch
        verify(mockCompleteSearchRepository, times(0)).save(complete);
    }

    @Test
    void getAllCompletesAsStream() {
        // Initialize the database
        complete.setId(UUID.randomUUID().toString());
        completeRepository.save(complete).block();

        List<Complete> completeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(CompleteDTO.class)
            .getResponseBody()
            .map(completeMapper::toEntity)
            .filter(complete::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(completeList).isNotNull();
        assertThat(completeList).hasSize(1);
        Complete testComplete = completeList.get(0);
        assertThat(testComplete.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testComplete.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void getAllCompletes() {
        // Initialize the database
        complete.setId(UUID.randomUUID().toString());
        completeRepository.save(complete).block();

        // Get all the completeList
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
            .value(hasItem(complete.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].enabled")
            .value(hasItem(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    void getComplete() {
        // Initialize the database
        complete.setId(UUID.randomUUID().toString());
        completeRepository.save(complete).block();

        // Get the complete
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, complete.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(complete.getId()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.enabled")
            .value(is(DEFAULT_ENABLED.booleanValue()));
    }

    @Test
    void getNonExistingComplete() {
        // Get the complete
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewComplete() throws Exception {
        // Configure the mock search repository
        when(mockCompleteSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        complete.setId(UUID.randomUUID().toString());
        completeRepository.save(complete).block();

        int databaseSizeBeforeUpdate = completeRepository.findAll().collectList().block().size();

        // Update the complete
        Complete updatedComplete = completeRepository.findById(complete.getId()).block();
        updatedComplete.description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);
        CompleteDTO completeDTO = completeMapper.toDto(updatedComplete);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, completeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(completeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Complete in the database
        List<Complete> completeList = completeRepository.findAll().collectList().block();
        assertThat(completeList).hasSize(databaseSizeBeforeUpdate);
        Complete testComplete = completeList.get(completeList.size() - 1);
        assertThat(testComplete.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testComplete.getEnabled()).isEqualTo(UPDATED_ENABLED);

        // Validate the Complete in Elasticsearch
        verify(mockCompleteSearchRepository).save(testComplete);
    }

    @Test
    void putNonExistingComplete() throws Exception {
        int databaseSizeBeforeUpdate = completeRepository.findAll().collectList().block().size();
        complete.setId(UUID.randomUUID().toString());

        // Create the Complete
        CompleteDTO completeDTO = completeMapper.toDto(complete);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, completeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(completeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Complete in the database
        List<Complete> completeList = completeRepository.findAll().collectList().block();
        assertThat(completeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Complete in Elasticsearch
        verify(mockCompleteSearchRepository, times(0)).save(complete);
    }

    @Test
    void putWithIdMismatchComplete() throws Exception {
        int databaseSizeBeforeUpdate = completeRepository.findAll().collectList().block().size();
        complete.setId(UUID.randomUUID().toString());

        // Create the Complete
        CompleteDTO completeDTO = completeMapper.toDto(complete);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(completeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Complete in the database
        List<Complete> completeList = completeRepository.findAll().collectList().block();
        assertThat(completeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Complete in Elasticsearch
        verify(mockCompleteSearchRepository, times(0)).save(complete);
    }

    @Test
    void putWithMissingIdPathParamComplete() throws Exception {
        int databaseSizeBeforeUpdate = completeRepository.findAll().collectList().block().size();
        complete.setId(UUID.randomUUID().toString());

        // Create the Complete
        CompleteDTO completeDTO = completeMapper.toDto(complete);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(completeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Complete in the database
        List<Complete> completeList = completeRepository.findAll().collectList().block();
        assertThat(completeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Complete in Elasticsearch
        verify(mockCompleteSearchRepository, times(0)).save(complete);
    }

    @Test
    void partialUpdateCompleteWithPatch() throws Exception {
        // Initialize the database
        complete.setId(UUID.randomUUID().toString());
        completeRepository.save(complete).block();

        int databaseSizeBeforeUpdate = completeRepository.findAll().collectList().block().size();

        // Update the complete using partial update
        Complete partialUpdatedComplete = new Complete();
        partialUpdatedComplete.setId(complete.getId());

        partialUpdatedComplete.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedComplete.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedComplete))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Complete in the database
        List<Complete> completeList = completeRepository.findAll().collectList().block();
        assertThat(completeList).hasSize(databaseSizeBeforeUpdate);
        Complete testComplete = completeList.get(completeList.size() - 1);
        assertThat(testComplete.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testComplete.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    void fullUpdateCompleteWithPatch() throws Exception {
        // Initialize the database
        complete.setId(UUID.randomUUID().toString());
        completeRepository.save(complete).block();

        int databaseSizeBeforeUpdate = completeRepository.findAll().collectList().block().size();

        // Update the complete using partial update
        Complete partialUpdatedComplete = new Complete();
        partialUpdatedComplete.setId(complete.getId());

        partialUpdatedComplete.description(UPDATED_DESCRIPTION).enabled(UPDATED_ENABLED);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedComplete.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedComplete))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Complete in the database
        List<Complete> completeList = completeRepository.findAll().collectList().block();
        assertThat(completeList).hasSize(databaseSizeBeforeUpdate);
        Complete testComplete = completeList.get(completeList.size() - 1);
        assertThat(testComplete.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testComplete.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    void patchNonExistingComplete() throws Exception {
        int databaseSizeBeforeUpdate = completeRepository.findAll().collectList().block().size();
        complete.setId(UUID.randomUUID().toString());

        // Create the Complete
        CompleteDTO completeDTO = completeMapper.toDto(complete);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, completeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(completeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Complete in the database
        List<Complete> completeList = completeRepository.findAll().collectList().block();
        assertThat(completeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Complete in Elasticsearch
        verify(mockCompleteSearchRepository, times(0)).save(complete);
    }

    @Test
    void patchWithIdMismatchComplete() throws Exception {
        int databaseSizeBeforeUpdate = completeRepository.findAll().collectList().block().size();
        complete.setId(UUID.randomUUID().toString());

        // Create the Complete
        CompleteDTO completeDTO = completeMapper.toDto(complete);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(completeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Complete in the database
        List<Complete> completeList = completeRepository.findAll().collectList().block();
        assertThat(completeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Complete in Elasticsearch
        verify(mockCompleteSearchRepository, times(0)).save(complete);
    }

    @Test
    void patchWithMissingIdPathParamComplete() throws Exception {
        int databaseSizeBeforeUpdate = completeRepository.findAll().collectList().block().size();
        complete.setId(UUID.randomUUID().toString());

        // Create the Complete
        CompleteDTO completeDTO = completeMapper.toDto(complete);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(completeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Complete in the database
        List<Complete> completeList = completeRepository.findAll().collectList().block();
        assertThat(completeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Complete in Elasticsearch
        verify(mockCompleteSearchRepository, times(0)).save(complete);
    }

    @Test
    void deleteComplete() {
        // Configure the mock search repository
        when(mockCompleteSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockCompleteSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        complete.setId(UUID.randomUUID().toString());
        completeRepository.save(complete).block();

        int databaseSizeBeforeDelete = completeRepository.findAll().collectList().block().size();

        // Delete the complete
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, complete.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Complete> completeList = completeRepository.findAll().collectList().block();
        assertThat(completeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Complete in Elasticsearch
        verify(mockCompleteSearchRepository, times(1)).deleteById(complete.getId());
    }

    @Test
    void searchComplete() {
        // Configure the mock search repository
        when(mockCompleteSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        complete.setId(UUID.randomUUID().toString());
        completeRepository.save(complete).block();
        when(mockCompleteSearchRepository.search("id:" + complete.getId())).thenReturn(Flux.just(complete));

        // Search the complete
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + complete.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(complete.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].enabled")
            .value(hasItem(DEFAULT_ENABLED.booleanValue()));
    }
}
