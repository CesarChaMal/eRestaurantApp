package com.erestaurant.erestaurantapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.erestaurantapp.IntegrationTest;
import com.erestaurant.erestaurantapp.domain.NotificationType;
import com.erestaurant.erestaurantapp.repository.EntityManager;
import com.erestaurant.erestaurantapp.repository.NotificationTypeRepository;
import com.erestaurant.erestaurantapp.repository.search.NotificationTypeSearchRepository;
import com.erestaurant.erestaurantapp.service.dto.NotificationTypeDTO;
import com.erestaurant.erestaurantapp.service.mapper.NotificationTypeMapper;
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
 * Integration tests for the {@link NotificationTypeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class NotificationTypeResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/notification-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/notification-types";

    @Autowired
    private NotificationTypeRepository notificationTypeRepository;

    @Autowired
    private NotificationTypeMapper notificationTypeMapper;

    /**
     * This repository is mocked in the com.erestaurant.erestaurantapp.repository.search test package.
     *
     * @see com.erestaurant.erestaurantapp.repository.search.NotificationTypeSearchRepositoryMockConfiguration
     */
    @Autowired
    private NotificationTypeSearchRepository mockNotificationTypeSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private NotificationType notificationType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationType createEntity(EntityManager em) {
        NotificationType notificationType = new NotificationType().description(DEFAULT_DESCRIPTION);
        return notificationType;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationType createUpdatedEntity(EntityManager em) {
        NotificationType notificationType = new NotificationType().description(UPDATED_DESCRIPTION);
        return notificationType;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(NotificationType.class).block();
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
        notificationType = createEntity(em);
    }

    @Test
    void createNotificationType() throws Exception {
        int databaseSizeBeforeCreate = notificationTypeRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockNotificationTypeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the NotificationType
        NotificationTypeDTO notificationTypeDTO = notificationTypeMapper.toDto(notificationType);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationTypeDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the NotificationType in the database
        List<NotificationType> notificationTypeList = notificationTypeRepository.findAll().collectList().block();
        assertThat(notificationTypeList).hasSize(databaseSizeBeforeCreate + 1);
        NotificationType testNotificationType = notificationTypeList.get(notificationTypeList.size() - 1);
        assertThat(testNotificationType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the NotificationType in Elasticsearch
        verify(mockNotificationTypeSearchRepository, times(1)).save(testNotificationType);
    }

    @Test
    void createNotificationTypeWithExistingId() throws Exception {
        // Create the NotificationType with an existing ID
        notificationType.setId("existing_id");
        NotificationTypeDTO notificationTypeDTO = notificationTypeMapper.toDto(notificationType);

        int databaseSizeBeforeCreate = notificationTypeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the NotificationType in the database
        List<NotificationType> notificationTypeList = notificationTypeRepository.findAll().collectList().block();
        assertThat(notificationTypeList).hasSize(databaseSizeBeforeCreate);

        // Validate the NotificationType in Elasticsearch
        verify(mockNotificationTypeSearchRepository, times(0)).save(notificationType);
    }

    @Test
    void getAllNotificationTypesAsStream() {
        // Initialize the database
        notificationType.setId(UUID.randomUUID().toString());
        notificationTypeRepository.save(notificationType).block();

        List<NotificationType> notificationTypeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(NotificationTypeDTO.class)
            .getResponseBody()
            .map(notificationTypeMapper::toEntity)
            .filter(notificationType::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(notificationTypeList).isNotNull();
        assertThat(notificationTypeList).hasSize(1);
        NotificationType testNotificationType = notificationTypeList.get(0);
        assertThat(testNotificationType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllNotificationTypes() {
        // Initialize the database
        notificationType.setId(UUID.randomUUID().toString());
        notificationTypeRepository.save(notificationType).block();

        // Get all the notificationTypeList
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
            .value(hasItem(notificationType.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getNotificationType() {
        // Initialize the database
        notificationType.setId(UUID.randomUUID().toString());
        notificationTypeRepository.save(notificationType).block();

        // Get the notificationType
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, notificationType.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(notificationType.getId()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getNonExistingNotificationType() {
        // Get the notificationType
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewNotificationType() throws Exception {
        // Configure the mock search repository
        when(mockNotificationTypeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        notificationType.setId(UUID.randomUUID().toString());
        notificationTypeRepository.save(notificationType).block();

        int databaseSizeBeforeUpdate = notificationTypeRepository.findAll().collectList().block().size();

        // Update the notificationType
        NotificationType updatedNotificationType = notificationTypeRepository.findById(notificationType.getId()).block();
        updatedNotificationType.description(UPDATED_DESCRIPTION);
        NotificationTypeDTO notificationTypeDTO = notificationTypeMapper.toDto(updatedNotificationType);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, notificationTypeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationTypeDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the NotificationType in the database
        List<NotificationType> notificationTypeList = notificationTypeRepository.findAll().collectList().block();
        assertThat(notificationTypeList).hasSize(databaseSizeBeforeUpdate);
        NotificationType testNotificationType = notificationTypeList.get(notificationTypeList.size() - 1);
        assertThat(testNotificationType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the NotificationType in Elasticsearch
        verify(mockNotificationTypeSearchRepository).save(testNotificationType);
    }

    @Test
    void putNonExistingNotificationType() throws Exception {
        int databaseSizeBeforeUpdate = notificationTypeRepository.findAll().collectList().block().size();
        notificationType.setId(UUID.randomUUID().toString());

        // Create the NotificationType
        NotificationTypeDTO notificationTypeDTO = notificationTypeMapper.toDto(notificationType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, notificationTypeDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the NotificationType in the database
        List<NotificationType> notificationTypeList = notificationTypeRepository.findAll().collectList().block();
        assertThat(notificationTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the NotificationType in Elasticsearch
        verify(mockNotificationTypeSearchRepository, times(0)).save(notificationType);
    }

    @Test
    void putWithIdMismatchNotificationType() throws Exception {
        int databaseSizeBeforeUpdate = notificationTypeRepository.findAll().collectList().block().size();
        notificationType.setId(UUID.randomUUID().toString());

        // Create the NotificationType
        NotificationTypeDTO notificationTypeDTO = notificationTypeMapper.toDto(notificationType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the NotificationType in the database
        List<NotificationType> notificationTypeList = notificationTypeRepository.findAll().collectList().block();
        assertThat(notificationTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the NotificationType in Elasticsearch
        verify(mockNotificationTypeSearchRepository, times(0)).save(notificationType);
    }

    @Test
    void putWithMissingIdPathParamNotificationType() throws Exception {
        int databaseSizeBeforeUpdate = notificationTypeRepository.findAll().collectList().block().size();
        notificationType.setId(UUID.randomUUID().toString());

        // Create the NotificationType
        NotificationTypeDTO notificationTypeDTO = notificationTypeMapper.toDto(notificationType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationTypeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the NotificationType in the database
        List<NotificationType> notificationTypeList = notificationTypeRepository.findAll().collectList().block();
        assertThat(notificationTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the NotificationType in Elasticsearch
        verify(mockNotificationTypeSearchRepository, times(0)).save(notificationType);
    }

    @Test
    void partialUpdateNotificationTypeWithPatch() throws Exception {
        // Initialize the database
        notificationType.setId(UUID.randomUUID().toString());
        notificationTypeRepository.save(notificationType).block();

        int databaseSizeBeforeUpdate = notificationTypeRepository.findAll().collectList().block().size();

        // Update the notificationType using partial update
        NotificationType partialUpdatedNotificationType = new NotificationType();
        partialUpdatedNotificationType.setId(notificationType.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNotificationType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNotificationType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the NotificationType in the database
        List<NotificationType> notificationTypeList = notificationTypeRepository.findAll().collectList().block();
        assertThat(notificationTypeList).hasSize(databaseSizeBeforeUpdate);
        NotificationType testNotificationType = notificationTypeList.get(notificationTypeList.size() - 1);
        assertThat(testNotificationType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void fullUpdateNotificationTypeWithPatch() throws Exception {
        // Initialize the database
        notificationType.setId(UUID.randomUUID().toString());
        notificationTypeRepository.save(notificationType).block();

        int databaseSizeBeforeUpdate = notificationTypeRepository.findAll().collectList().block().size();

        // Update the notificationType using partial update
        NotificationType partialUpdatedNotificationType = new NotificationType();
        partialUpdatedNotificationType.setId(notificationType.getId());

        partialUpdatedNotificationType.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNotificationType.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNotificationType))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the NotificationType in the database
        List<NotificationType> notificationTypeList = notificationTypeRepository.findAll().collectList().block();
        assertThat(notificationTypeList).hasSize(databaseSizeBeforeUpdate);
        NotificationType testNotificationType = notificationTypeList.get(notificationTypeList.size() - 1);
        assertThat(testNotificationType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingNotificationType() throws Exception {
        int databaseSizeBeforeUpdate = notificationTypeRepository.findAll().collectList().block().size();
        notificationType.setId(UUID.randomUUID().toString());

        // Create the NotificationType
        NotificationTypeDTO notificationTypeDTO = notificationTypeMapper.toDto(notificationType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, notificationTypeDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the NotificationType in the database
        List<NotificationType> notificationTypeList = notificationTypeRepository.findAll().collectList().block();
        assertThat(notificationTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the NotificationType in Elasticsearch
        verify(mockNotificationTypeSearchRepository, times(0)).save(notificationType);
    }

    @Test
    void patchWithIdMismatchNotificationType() throws Exception {
        int databaseSizeBeforeUpdate = notificationTypeRepository.findAll().collectList().block().size();
        notificationType.setId(UUID.randomUUID().toString());

        // Create the NotificationType
        NotificationTypeDTO notificationTypeDTO = notificationTypeMapper.toDto(notificationType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationTypeDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the NotificationType in the database
        List<NotificationType> notificationTypeList = notificationTypeRepository.findAll().collectList().block();
        assertThat(notificationTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the NotificationType in Elasticsearch
        verify(mockNotificationTypeSearchRepository, times(0)).save(notificationType);
    }

    @Test
    void patchWithMissingIdPathParamNotificationType() throws Exception {
        int databaseSizeBeforeUpdate = notificationTypeRepository.findAll().collectList().block().size();
        notificationType.setId(UUID.randomUUID().toString());

        // Create the NotificationType
        NotificationTypeDTO notificationTypeDTO = notificationTypeMapper.toDto(notificationType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationTypeDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the NotificationType in the database
        List<NotificationType> notificationTypeList = notificationTypeRepository.findAll().collectList().block();
        assertThat(notificationTypeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the NotificationType in Elasticsearch
        verify(mockNotificationTypeSearchRepository, times(0)).save(notificationType);
    }

    @Test
    void deleteNotificationType() {
        // Configure the mock search repository
        when(mockNotificationTypeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockNotificationTypeSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        notificationType.setId(UUID.randomUUID().toString());
        notificationTypeRepository.save(notificationType).block();

        int databaseSizeBeforeDelete = notificationTypeRepository.findAll().collectList().block().size();

        // Delete the notificationType
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, notificationType.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<NotificationType> notificationTypeList = notificationTypeRepository.findAll().collectList().block();
        assertThat(notificationTypeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the NotificationType in Elasticsearch
        verify(mockNotificationTypeSearchRepository, times(1)).deleteById(notificationType.getId());
    }

    @Test
    void searchNotificationType() {
        // Configure the mock search repository
        when(mockNotificationTypeSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        notificationType.setId(UUID.randomUUID().toString());
        notificationTypeRepository.save(notificationType).block();
        when(mockNotificationTypeSearchRepository.search("id:" + notificationType.getId())).thenReturn(Flux.just(notificationType));

        // Search the notificationType
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + notificationType.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(notificationType.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }
}
