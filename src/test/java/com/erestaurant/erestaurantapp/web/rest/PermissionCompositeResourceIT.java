package com.erestaurant.erestaurantapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.erestaurantapp.IntegrationTest;
import com.erestaurant.erestaurantapp.domain.PermissionComposite;
import com.erestaurant.erestaurantapp.repository.EntityManager;
import com.erestaurant.erestaurantapp.repository.PermissionCompositeRepository;
import com.erestaurant.erestaurantapp.repository.search.PermissionCompositeSearchRepository;
import com.erestaurant.erestaurantapp.service.dto.PermissionCompositeDTO;
import com.erestaurant.erestaurantapp.service.mapper.PermissionCompositeMapper;
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
 * Integration tests for the {@link PermissionCompositeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PermissionCompositeResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/permission-composites";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/permission-composites";

    @Autowired
    private PermissionCompositeRepository permissionCompositeRepository;

    @Autowired
    private PermissionCompositeMapper permissionCompositeMapper;

    /**
     * This repository is mocked in the com.erestaurant.erestaurantapp.repository.search test package.
     *
     * @see com.erestaurant.erestaurantapp.repository.search.PermissionCompositeSearchRepositoryMockConfiguration
     */
    @Autowired
    private PermissionCompositeSearchRepository mockPermissionCompositeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PermissionComposite permissionComposite;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PermissionComposite createEntity(EntityManager em) {
        PermissionComposite permissionComposite = new PermissionComposite().description(DEFAULT_DESCRIPTION);
        return permissionComposite;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PermissionComposite createUpdatedEntity(EntityManager em) {
        PermissionComposite permissionComposite = new PermissionComposite().description(UPDATED_DESCRIPTION);
        return permissionComposite;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PermissionComposite.class).block();
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
        permissionComposite = createEntity(em);
    }

    @Test
    void createPermissionComposite() throws Exception {
        int databaseSizeBeforeCreate = permissionCompositeRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockPermissionCompositeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the PermissionComposite
        PermissionCompositeDTO permissionCompositeDTO = permissionCompositeMapper.toDto(permissionComposite);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionCompositeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PermissionComposite in the database
        List<PermissionComposite> permissionCompositeList = permissionCompositeRepository.findAll().collectList().block();
        assertThat(permissionCompositeList).hasSize(databaseSizeBeforeCreate + 1);
        PermissionComposite testPermissionComposite = permissionCompositeList.get(permissionCompositeList.size() - 1);
        assertThat(testPermissionComposite.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the PermissionComposite in Elasticsearch
        verify(mockPermissionCompositeSearchRepository, times(1)).save(testPermissionComposite);
    }

    @Test
    void createPermissionCompositeWithExistingId() throws Exception {
        // Create the PermissionComposite with an existing ID
        permissionComposite.setId("existing_id");
        PermissionCompositeDTO permissionCompositeDTO = permissionCompositeMapper.toDto(permissionComposite);

        int databaseSizeBeforeCreate = permissionCompositeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionCompositeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PermissionComposite in the database
        List<PermissionComposite> permissionCompositeList = permissionCompositeRepository.findAll().collectList().block();
        assertThat(permissionCompositeList).hasSize(databaseSizeBeforeCreate);

        // Validate the PermissionComposite in Elasticsearch
        verify(mockPermissionCompositeSearchRepository, times(0)).save(permissionComposite);
    }

    @Test
    void getAllPermissionCompositesAsStream() {
        // Initialize the database
        permissionComposite.setId(UUID.randomUUID().toString());
        permissionCompositeRepository.save(permissionComposite).block();

        List<PermissionComposite> permissionCompositeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PermissionCompositeDTO.class)
            .getResponseBody()
            .map(permissionCompositeMapper::toEntity)
            .filter(permissionComposite::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(permissionCompositeList).isNotNull();
        assertThat(permissionCompositeList).hasSize(1);
        PermissionComposite testPermissionComposite = permissionCompositeList.get(0);
        assertThat(testPermissionComposite.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllPermissionComposites() {
        // Initialize the database
        permissionComposite.setId(UUID.randomUUID().toString());
        permissionCompositeRepository.save(permissionComposite).block();

        // Get all the permissionCompositeList
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
            .value(hasItem(permissionComposite.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getPermissionComposite() {
        // Initialize the database
        permissionComposite.setId(UUID.randomUUID().toString());
        permissionCompositeRepository.save(permissionComposite).block();

        // Get the permissionComposite
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, permissionComposite.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(permissionComposite.getId()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getNonExistingPermissionComposite() {
        // Get the permissionComposite
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPermissionComposite() throws Exception {
        // Configure the mock search repository
        when(mockPermissionCompositeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        permissionComposite.setId(UUID.randomUUID().toString());
        permissionCompositeRepository.save(permissionComposite).block();

        int databaseSizeBeforeUpdate = permissionCompositeRepository.findAll().collectList().block().size();

        // Update the permissionComposite
        PermissionComposite updatedPermissionComposite = permissionCompositeRepository.findById(permissionComposite.getId()).block();
        updatedPermissionComposite.description(UPDATED_DESCRIPTION);
        PermissionCompositeDTO permissionCompositeDTO = permissionCompositeMapper.toDto(updatedPermissionComposite);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, permissionCompositeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionCompositeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PermissionComposite in the database
        List<PermissionComposite> permissionCompositeList = permissionCompositeRepository.findAll().collectList().block();
        assertThat(permissionCompositeList).hasSize(databaseSizeBeforeUpdate);
        PermissionComposite testPermissionComposite = permissionCompositeList.get(permissionCompositeList.size() - 1);
        assertThat(testPermissionComposite.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the PermissionComposite in Elasticsearch
        verify(mockPermissionCompositeSearchRepository).save(testPermissionComposite);
    }

    @Test
    void putNonExistingPermissionComposite() throws Exception {
        int databaseSizeBeforeUpdate = permissionCompositeRepository.findAll().collectList().block().size();
        permissionComposite.setId(UUID.randomUUID().toString());

        // Create the PermissionComposite
        PermissionCompositeDTO permissionCompositeDTO = permissionCompositeMapper.toDto(permissionComposite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, permissionCompositeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionCompositeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PermissionComposite in the database
        List<PermissionComposite> permissionCompositeList = permissionCompositeRepository.findAll().collectList().block();
        assertThat(permissionCompositeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PermissionComposite in Elasticsearch
        verify(mockPermissionCompositeSearchRepository, times(0)).save(permissionComposite);
    }

    @Test
    void putWithIdMismatchPermissionComposite() throws Exception {
        int databaseSizeBeforeUpdate = permissionCompositeRepository.findAll().collectList().block().size();
        permissionComposite.setId(UUID.randomUUID().toString());

        // Create the PermissionComposite
        PermissionCompositeDTO permissionCompositeDTO = permissionCompositeMapper.toDto(permissionComposite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionCompositeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PermissionComposite in the database
        List<PermissionComposite> permissionCompositeList = permissionCompositeRepository.findAll().collectList().block();
        assertThat(permissionCompositeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PermissionComposite in Elasticsearch
        verify(mockPermissionCompositeSearchRepository, times(0)).save(permissionComposite);
    }

    @Test
    void putWithMissingIdPathParamPermissionComposite() throws Exception {
        int databaseSizeBeforeUpdate = permissionCompositeRepository.findAll().collectList().block().size();
        permissionComposite.setId(UUID.randomUUID().toString());

        // Create the PermissionComposite
        PermissionCompositeDTO permissionCompositeDTO = permissionCompositeMapper.toDto(permissionComposite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionCompositeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PermissionComposite in the database
        List<PermissionComposite> permissionCompositeList = permissionCompositeRepository.findAll().collectList().block();
        assertThat(permissionCompositeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PermissionComposite in Elasticsearch
        verify(mockPermissionCompositeSearchRepository, times(0)).save(permissionComposite);
    }

    @Test
    void partialUpdatePermissionCompositeWithPatch() throws Exception {
        // Initialize the database
        permissionComposite.setId(UUID.randomUUID().toString());
        permissionCompositeRepository.save(permissionComposite).block();

        int databaseSizeBeforeUpdate = permissionCompositeRepository.findAll().collectList().block().size();

        // Update the permissionComposite using partial update
        PermissionComposite partialUpdatedPermissionComposite = new PermissionComposite();
        partialUpdatedPermissionComposite.setId(permissionComposite.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPermissionComposite.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPermissionComposite))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PermissionComposite in the database
        List<PermissionComposite> permissionCompositeList = permissionCompositeRepository.findAll().collectList().block();
        assertThat(permissionCompositeList).hasSize(databaseSizeBeforeUpdate);
        PermissionComposite testPermissionComposite = permissionCompositeList.get(permissionCompositeList.size() - 1);
        assertThat(testPermissionComposite.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void fullUpdatePermissionCompositeWithPatch() throws Exception {
        // Initialize the database
        permissionComposite.setId(UUID.randomUUID().toString());
        permissionCompositeRepository.save(permissionComposite).block();

        int databaseSizeBeforeUpdate = permissionCompositeRepository.findAll().collectList().block().size();

        // Update the permissionComposite using partial update
        PermissionComposite partialUpdatedPermissionComposite = new PermissionComposite();
        partialUpdatedPermissionComposite.setId(permissionComposite.getId());

        partialUpdatedPermissionComposite.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPermissionComposite.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPermissionComposite))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PermissionComposite in the database
        List<PermissionComposite> permissionCompositeList = permissionCompositeRepository.findAll().collectList().block();
        assertThat(permissionCompositeList).hasSize(databaseSizeBeforeUpdate);
        PermissionComposite testPermissionComposite = permissionCompositeList.get(permissionCompositeList.size() - 1);
        assertThat(testPermissionComposite.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingPermissionComposite() throws Exception {
        int databaseSizeBeforeUpdate = permissionCompositeRepository.findAll().collectList().block().size();
        permissionComposite.setId(UUID.randomUUID().toString());

        // Create the PermissionComposite
        PermissionCompositeDTO permissionCompositeDTO = permissionCompositeMapper.toDto(permissionComposite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, permissionCompositeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionCompositeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PermissionComposite in the database
        List<PermissionComposite> permissionCompositeList = permissionCompositeRepository.findAll().collectList().block();
        assertThat(permissionCompositeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PermissionComposite in Elasticsearch
        verify(mockPermissionCompositeSearchRepository, times(0)).save(permissionComposite);
    }

    @Test
    void patchWithIdMismatchPermissionComposite() throws Exception {
        int databaseSizeBeforeUpdate = permissionCompositeRepository.findAll().collectList().block().size();
        permissionComposite.setId(UUID.randomUUID().toString());

        // Create the PermissionComposite
        PermissionCompositeDTO permissionCompositeDTO = permissionCompositeMapper.toDto(permissionComposite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionCompositeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PermissionComposite in the database
        List<PermissionComposite> permissionCompositeList = permissionCompositeRepository.findAll().collectList().block();
        assertThat(permissionCompositeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PermissionComposite in Elasticsearch
        verify(mockPermissionCompositeSearchRepository, times(0)).save(permissionComposite);
    }

    @Test
    void patchWithMissingIdPathParamPermissionComposite() throws Exception {
        int databaseSizeBeforeUpdate = permissionCompositeRepository.findAll().collectList().block().size();
        permissionComposite.setId(UUID.randomUUID().toString());

        // Create the PermissionComposite
        PermissionCompositeDTO permissionCompositeDTO = permissionCompositeMapper.toDto(permissionComposite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionCompositeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PermissionComposite in the database
        List<PermissionComposite> permissionCompositeList = permissionCompositeRepository.findAll().collectList().block();
        assertThat(permissionCompositeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PermissionComposite in Elasticsearch
        verify(mockPermissionCompositeSearchRepository, times(0)).save(permissionComposite);
    }

    @Test
    void deletePermissionComposite() {
        // Configure the mock search repository
        when(mockPermissionCompositeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockPermissionCompositeSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        permissionComposite.setId(UUID.randomUUID().toString());
        permissionCompositeRepository.save(permissionComposite).block();

        int databaseSizeBeforeDelete = permissionCompositeRepository.findAll().collectList().block().size();

        // Delete the permissionComposite
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, permissionComposite.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PermissionComposite> permissionCompositeList = permissionCompositeRepository.findAll().collectList().block();
        assertThat(permissionCompositeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PermissionComposite in Elasticsearch
        verify(mockPermissionCompositeSearchRepository, times(1)).deleteById(permissionComposite.getId());
    }

    @Test
    void searchPermissionComposite() {
        // Configure the mock search repository
        when(mockPermissionCompositeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        permissionComposite.setId(UUID.randomUUID().toString());
        permissionCompositeRepository.save(permissionComposite).block();
        when(mockPermissionCompositeSearchRepository.search("id:" + permissionComposite.getId()))
            .thenReturn(Flux.just(permissionComposite));

        // Search the permissionComposite
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + permissionComposite.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(permissionComposite.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }
}
