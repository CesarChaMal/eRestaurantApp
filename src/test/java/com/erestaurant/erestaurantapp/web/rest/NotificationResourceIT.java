package com.erestaurant.erestaurantapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.erestaurantapp.IntegrationTest;
import com.erestaurant.erestaurantapp.domain.Notification;
import com.erestaurant.erestaurantapp.repository.EntityManager;
import com.erestaurant.erestaurantapp.repository.NotificationRepository;
import com.erestaurant.erestaurantapp.repository.search.NotificationSearchRepository;
import com.erestaurant.erestaurantapp.service.dto.NotificationDTO;
import com.erestaurant.erestaurantapp.service.mapper.NotificationMapper;
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
 * Integration tests for the {@link NotificationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class NotificationResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/notifications";

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * This repository is mocked in the com.erestaurant.erestaurantapp.repository.search test package.
     *
     * @see com.erestaurant.erestaurantapp.repository.search.NotificationSearchRepositoryMockConfiguration
     */
    @Autowired
    private NotificationSearchRepository mockNotificationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Notification notification;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createEntity(EntityManager em) {
        Notification notification = new Notification().description(DEFAULT_DESCRIPTION);
        return notification;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createUpdatedEntity(EntityManager em) {
        Notification notification = new Notification().description(UPDATED_DESCRIPTION);
        return notification;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Notification.class).block();
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
        notification = createEntity(em);
    }

    @Test
    void createNotification() throws Exception {
        int databaseSizeBeforeCreate = notificationRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockNotificationSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate + 1);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Notification in Elasticsearch
        verify(mockNotificationSearchRepository, times(1)).save(testNotification);
    }

    @Test
    void createNotificationWithExistingId() throws Exception {
        // Create the Notification with an existing ID
        notification.setId("existing_id");
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        int databaseSizeBeforeCreate = notificationRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate);

        // Validate the Notification in Elasticsearch
        verify(mockNotificationSearchRepository, times(0)).save(notification);
    }

    @Test
    void getAllNotificationsAsStream() {
        // Initialize the database
        notification.setId(UUID.randomUUID().toString());
        notificationRepository.save(notification).block();

        List<Notification> notificationList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(NotificationDTO.class)
            .getResponseBody()
            .map(notificationMapper::toEntity)
            .filter(notification::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(notificationList).isNotNull();
        assertThat(notificationList).hasSize(1);
        Notification testNotification = notificationList.get(0);
        assertThat(testNotification.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllNotifications() {
        // Initialize the database
        notification.setId(UUID.randomUUID().toString());
        notificationRepository.save(notification).block();

        // Get all the notificationList
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
            .value(hasItem(notification.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getNotification() {
        // Initialize the database
        notification.setId(UUID.randomUUID().toString());
        notificationRepository.save(notification).block();

        // Get the notification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, notification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(notification.getId()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getNonExistingNotification() {
        // Get the notification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewNotification() throws Exception {
        // Configure the mock search repository
        when(mockNotificationSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        notification.setId(UUID.randomUUID().toString());
        notificationRepository.save(notification).block();

        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();

        // Update the notification
        Notification updatedNotification = notificationRepository.findById(notification.getId()).block();
        updatedNotification.description(UPDATED_DESCRIPTION);
        NotificationDTO notificationDTO = notificationMapper.toDto(updatedNotification);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, notificationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Notification in Elasticsearch
        verify(mockNotificationSearchRepository).save(testNotification);
    }

    @Test
    void putNonExistingNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        notification.setId(UUID.randomUUID().toString());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, notificationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Notification in Elasticsearch
        verify(mockNotificationSearchRepository, times(0)).save(notification);
    }

    @Test
    void putWithIdMismatchNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        notification.setId(UUID.randomUUID().toString());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Notification in Elasticsearch
        verify(mockNotificationSearchRepository, times(0)).save(notification);
    }

    @Test
    void putWithMissingIdPathParamNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        notification.setId(UUID.randomUUID().toString());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Notification in Elasticsearch
        verify(mockNotificationSearchRepository, times(0)).save(notification);
    }

    @Test
    void partialUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        notification.setId(UUID.randomUUID().toString());
        notificationRepository.save(notification).block();

        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNotification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void fullUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        notification.setId(UUID.randomUUID().toString());
        notificationRepository.save(notification).block();

        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNotification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        notification.setId(UUID.randomUUID().toString());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, notificationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Notification in Elasticsearch
        verify(mockNotificationSearchRepository, times(0)).save(notification);
    }

    @Test
    void patchWithIdMismatchNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        notification.setId(UUID.randomUUID().toString());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Notification in Elasticsearch
        verify(mockNotificationSearchRepository, times(0)).save(notification);
    }

    @Test
    void patchWithMissingIdPathParamNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().collectList().block().size();
        notification.setId(UUID.randomUUID().toString());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Notification in Elasticsearch
        verify(mockNotificationSearchRepository, times(0)).save(notification);
    }

    @Test
    void deleteNotification() {
        // Configure the mock search repository
        when(mockNotificationSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockNotificationSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        notification.setId(UUID.randomUUID().toString());
        notificationRepository.save(notification).block();

        int databaseSizeBeforeDelete = notificationRepository.findAll().collectList().block().size();

        // Delete the notification
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, notification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Notification> notificationList = notificationRepository.findAll().collectList().block();
        assertThat(notificationList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Notification in Elasticsearch
        verify(mockNotificationSearchRepository, times(1)).deleteById(notification.getId());
    }

    @Test
    void searchNotification() {
        // Configure the mock search repository
        when(mockNotificationSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        notification.setId(UUID.randomUUID().toString());
        notificationRepository.save(notification).block();
        when(mockNotificationSearchRepository.search("id:" + notification.getId())).thenReturn(Flux.just(notification));

        // Search the notification
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + notification.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(notification.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }
}
