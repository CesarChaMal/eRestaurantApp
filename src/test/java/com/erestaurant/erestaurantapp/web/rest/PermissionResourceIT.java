package com.erestaurant.erestaurantapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.erestaurantapp.IntegrationTest;
import com.erestaurant.erestaurantapp.domain.Permission;
import com.erestaurant.erestaurantapp.repository.EntityManager;
import com.erestaurant.erestaurantapp.repository.PermissionRepository;
import com.erestaurant.erestaurantapp.repository.search.PermissionSearchRepository;
import com.erestaurant.erestaurantapp.service.dto.PermissionDTO;
import com.erestaurant.erestaurantapp.service.mapper.PermissionMapper;
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
 * Integration tests for the {@link PermissionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PermissionResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/permissions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/permissions";

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionMapper permissionMapper;

    /**
     * This repository is mocked in the com.erestaurant.erestaurantapp.repository.search test package.
     *
     * @see com.erestaurant.erestaurantapp.repository.search.PermissionSearchRepositoryMockConfiguration
     */
    @Autowired
    private PermissionSearchRepository mockPermissionSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Permission permission;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Permission createEntity(EntityManager em) {
        Permission permission = new Permission().description(DEFAULT_DESCRIPTION);
        return permission;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Permission createUpdatedEntity(EntityManager em) {
        Permission permission = new Permission().description(UPDATED_DESCRIPTION);
        return permission;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Permission.class).block();
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
        permission = createEntity(em);
    }

    @Test
    void createPermission() throws Exception {
        int databaseSizeBeforeCreate = permissionRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockPermissionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Permission in the database
        List<Permission> permissionList = permissionRepository.findAll().collectList().block();
        assertThat(permissionList).hasSize(databaseSizeBeforeCreate + 1);
        Permission testPermission = permissionList.get(permissionList.size() - 1);
        assertThat(testPermission.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Permission in Elasticsearch
        verify(mockPermissionSearchRepository, times(1)).save(testPermission);
    }

    @Test
    void createPermissionWithExistingId() throws Exception {
        // Create the Permission with an existing ID
        permission.setId("existing_id");
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        int databaseSizeBeforeCreate = permissionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Permission in the database
        List<Permission> permissionList = permissionRepository.findAll().collectList().block();
        assertThat(permissionList).hasSize(databaseSizeBeforeCreate);

        // Validate the Permission in Elasticsearch
        verify(mockPermissionSearchRepository, times(0)).save(permission);
    }

    @Test
    void getAllPermissionsAsStream() {
        // Initialize the database
        permission.setId(UUID.randomUUID().toString());
        permissionRepository.save(permission).block();

        List<Permission> permissionList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PermissionDTO.class)
            .getResponseBody()
            .map(permissionMapper::toEntity)
            .filter(permission::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(permissionList).isNotNull();
        assertThat(permissionList).hasSize(1);
        Permission testPermission = permissionList.get(0);
        assertThat(testPermission.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllPermissions() {
        // Initialize the database
        permission.setId(UUID.randomUUID().toString());
        permissionRepository.save(permission).block();

        // Get all the permissionList
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
            .value(hasItem(permission.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getPermission() {
        // Initialize the database
        permission.setId(UUID.randomUUID().toString());
        permissionRepository.save(permission).block();

        // Get the permission
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, permission.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(permission.getId()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getNonExistingPermission() {
        // Get the permission
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPermission() throws Exception {
        // Configure the mock search repository
        when(mockPermissionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        permission.setId(UUID.randomUUID().toString());
        permissionRepository.save(permission).block();

        int databaseSizeBeforeUpdate = permissionRepository.findAll().collectList().block().size();

        // Update the permission
        Permission updatedPermission = permissionRepository.findById(permission.getId()).block();
        updatedPermission.description(UPDATED_DESCRIPTION);
        PermissionDTO permissionDTO = permissionMapper.toDto(updatedPermission);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, permissionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Permission in the database
        List<Permission> permissionList = permissionRepository.findAll().collectList().block();
        assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);
        Permission testPermission = permissionList.get(permissionList.size() - 1);
        assertThat(testPermission.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Permission in Elasticsearch
        verify(mockPermissionSearchRepository).save(testPermission);
    }

    @Test
    void putNonExistingPermission() throws Exception {
        int databaseSizeBeforeUpdate = permissionRepository.findAll().collectList().block().size();
        permission.setId(UUID.randomUUID().toString());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, permissionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Permission in the database
        List<Permission> permissionList = permissionRepository.findAll().collectList().block();
        assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Permission in Elasticsearch
        verify(mockPermissionSearchRepository, times(0)).save(permission);
    }

    @Test
    void putWithIdMismatchPermission() throws Exception {
        int databaseSizeBeforeUpdate = permissionRepository.findAll().collectList().block().size();
        permission.setId(UUID.randomUUID().toString());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Permission in the database
        List<Permission> permissionList = permissionRepository.findAll().collectList().block();
        assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Permission in Elasticsearch
        verify(mockPermissionSearchRepository, times(0)).save(permission);
    }

    @Test
    void putWithMissingIdPathParamPermission() throws Exception {
        int databaseSizeBeforeUpdate = permissionRepository.findAll().collectList().block().size();
        permission.setId(UUID.randomUUID().toString());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Permission in the database
        List<Permission> permissionList = permissionRepository.findAll().collectList().block();
        assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Permission in Elasticsearch
        verify(mockPermissionSearchRepository, times(0)).save(permission);
    }

    @Test
    void partialUpdatePermissionWithPatch() throws Exception {
        // Initialize the database
        permission.setId(UUID.randomUUID().toString());
        permissionRepository.save(permission).block();

        int databaseSizeBeforeUpdate = permissionRepository.findAll().collectList().block().size();

        // Update the permission using partial update
        Permission partialUpdatedPermission = new Permission();
        partialUpdatedPermission.setId(permission.getId());

        partialUpdatedPermission.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPermission.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPermission))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Permission in the database
        List<Permission> permissionList = permissionRepository.findAll().collectList().block();
        assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);
        Permission testPermission = permissionList.get(permissionList.size() - 1);
        assertThat(testPermission.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void fullUpdatePermissionWithPatch() throws Exception {
        // Initialize the database
        permission.setId(UUID.randomUUID().toString());
        permissionRepository.save(permission).block();

        int databaseSizeBeforeUpdate = permissionRepository.findAll().collectList().block().size();

        // Update the permission using partial update
        Permission partialUpdatedPermission = new Permission();
        partialUpdatedPermission.setId(permission.getId());

        partialUpdatedPermission.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPermission.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPermission))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Permission in the database
        List<Permission> permissionList = permissionRepository.findAll().collectList().block();
        assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);
        Permission testPermission = permissionList.get(permissionList.size() - 1);
        assertThat(testPermission.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingPermission() throws Exception {
        int databaseSizeBeforeUpdate = permissionRepository.findAll().collectList().block().size();
        permission.setId(UUID.randomUUID().toString());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, permissionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Permission in the database
        List<Permission> permissionList = permissionRepository.findAll().collectList().block();
        assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Permission in Elasticsearch
        verify(mockPermissionSearchRepository, times(0)).save(permission);
    }

    @Test
    void patchWithIdMismatchPermission() throws Exception {
        int databaseSizeBeforeUpdate = permissionRepository.findAll().collectList().block().size();
        permission.setId(UUID.randomUUID().toString());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Permission in the database
        List<Permission> permissionList = permissionRepository.findAll().collectList().block();
        assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Permission in Elasticsearch
        verify(mockPermissionSearchRepository, times(0)).save(permission);
    }

    @Test
    void patchWithMissingIdPathParamPermission() throws Exception {
        int databaseSizeBeforeUpdate = permissionRepository.findAll().collectList().block().size();
        permission.setId(UUID.randomUUID().toString());

        // Create the Permission
        PermissionDTO permissionDTO = permissionMapper.toDto(permission);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(permissionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Permission in the database
        List<Permission> permissionList = permissionRepository.findAll().collectList().block();
        assertThat(permissionList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Permission in Elasticsearch
        verify(mockPermissionSearchRepository, times(0)).save(permission);
    }

    @Test
    void deletePermission() {
        // Configure the mock search repository
        when(mockPermissionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockPermissionSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        permission.setId(UUID.randomUUID().toString());
        permissionRepository.save(permission).block();

        int databaseSizeBeforeDelete = permissionRepository.findAll().collectList().block().size();

        // Delete the permission
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, permission.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Permission> permissionList = permissionRepository.findAll().collectList().block();
        assertThat(permissionList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Permission in Elasticsearch
        verify(mockPermissionSearchRepository, times(1)).deleteById(permission.getId());
    }

    @Test
    void searchPermission() {
        // Configure the mock search repository
        when(mockPermissionSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        permission.setId(UUID.randomUUID().toString());
        permissionRepository.save(permission).block();
        when(mockPermissionSearchRepository.search("id:" + permission.getId())).thenReturn(Flux.just(permission));

        // Search the permission
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + permission.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(permission.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }
}
