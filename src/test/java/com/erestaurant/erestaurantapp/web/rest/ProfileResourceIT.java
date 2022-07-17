package com.erestaurant.erestaurantapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.erestaurantapp.IntegrationTest;
import com.erestaurant.erestaurantapp.domain.Profile;
import com.erestaurant.erestaurantapp.repository.EntityManager;
import com.erestaurant.erestaurantapp.repository.ProfileRepository;
import com.erestaurant.erestaurantapp.repository.search.ProfileSearchRepository;
import com.erestaurant.erestaurantapp.service.dto.ProfileDTO;
import com.erestaurant.erestaurantapp.service.mapper.ProfileMapper;
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
 * Integration tests for the {@link ProfileResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProfileResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/profiles";

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileMapper profileMapper;

    /**
     * This repository is mocked in the com.erestaurant.erestaurantapp.repository.search test package.
     *
     * @see com.erestaurant.erestaurantapp.repository.search.ProfileSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProfileSearchRepository mockProfileSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Profile profile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profile createEntity(EntityManager em) {
        Profile profile = new Profile().description(DEFAULT_DESCRIPTION);
        return profile;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Profile createUpdatedEntity(EntityManager em) {
        Profile profile = new Profile().description(UPDATED_DESCRIPTION);
        return profile;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Profile.class).block();
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
        profile = createEntity(em);
    }

    @Test
    void createProfile() throws Exception {
        int databaseSizeBeforeCreate = profileRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockProfileSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll().collectList().block();
        assertThat(profileList).hasSize(databaseSizeBeforeCreate + 1);
        Profile testProfile = profileList.get(profileList.size() - 1);
        assertThat(testProfile.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the Profile in Elasticsearch
        verify(mockProfileSearchRepository, times(1)).save(testProfile);
    }

    @Test
    void createProfileWithExistingId() throws Exception {
        // Create the Profile with an existing ID
        profile.setId("existing_id");
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        int databaseSizeBeforeCreate = profileRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll().collectList().block();
        assertThat(profileList).hasSize(databaseSizeBeforeCreate);

        // Validate the Profile in Elasticsearch
        verify(mockProfileSearchRepository, times(0)).save(profile);
    }

    @Test
    void getAllProfilesAsStream() {
        // Initialize the database
        profile.setId(UUID.randomUUID().toString());
        profileRepository.save(profile).block();

        List<Profile> profileList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ProfileDTO.class)
            .getResponseBody()
            .map(profileMapper::toEntity)
            .filter(profile::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(profileList).isNotNull();
        assertThat(profileList).hasSize(1);
        Profile testProfile = profileList.get(0);
        assertThat(testProfile.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllProfiles() {
        // Initialize the database
        profile.setId(UUID.randomUUID().toString());
        profileRepository.save(profile).block();

        // Get all the profileList
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
            .value(hasItem(profile.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getProfile() {
        // Initialize the database
        profile.setId(UUID.randomUUID().toString());
        profileRepository.save(profile).block();

        // Get the profile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, profile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(profile.getId()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getNonExistingProfile() {
        // Get the profile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewProfile() throws Exception {
        // Configure the mock search repository
        when(mockProfileSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        profile.setId(UUID.randomUUID().toString());
        profileRepository.save(profile).block();

        int databaseSizeBeforeUpdate = profileRepository.findAll().collectList().block().size();

        // Update the profile
        Profile updatedProfile = profileRepository.findById(profile.getId()).block();
        updatedProfile.description(UPDATED_DESCRIPTION);
        ProfileDTO profileDTO = profileMapper.toDto(updatedProfile);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, profileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll().collectList().block();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);
        Profile testProfile = profileList.get(profileList.size() - 1);
        assertThat(testProfile.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the Profile in Elasticsearch
        verify(mockProfileSearchRepository).save(testProfile);
    }

    @Test
    void putNonExistingProfile() throws Exception {
        int databaseSizeBeforeUpdate = profileRepository.findAll().collectList().block().size();
        profile.setId(UUID.randomUUID().toString());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, profileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll().collectList().block();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Profile in Elasticsearch
        verify(mockProfileSearchRepository, times(0)).save(profile);
    }

    @Test
    void putWithIdMismatchProfile() throws Exception {
        int databaseSizeBeforeUpdate = profileRepository.findAll().collectList().block().size();
        profile.setId(UUID.randomUUID().toString());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll().collectList().block();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Profile in Elasticsearch
        verify(mockProfileSearchRepository, times(0)).save(profile);
    }

    @Test
    void putWithMissingIdPathParamProfile() throws Exception {
        int databaseSizeBeforeUpdate = profileRepository.findAll().collectList().block().size();
        profile.setId(UUID.randomUUID().toString());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll().collectList().block();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Profile in Elasticsearch
        verify(mockProfileSearchRepository, times(0)).save(profile);
    }

    @Test
    void partialUpdateProfileWithPatch() throws Exception {
        // Initialize the database
        profile.setId(UUID.randomUUID().toString());
        profileRepository.save(profile).block();

        int databaseSizeBeforeUpdate = profileRepository.findAll().collectList().block().size();

        // Update the profile using partial update
        Profile partialUpdatedProfile = new Profile();
        partialUpdatedProfile.setId(profile.getId());

        partialUpdatedProfile.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll().collectList().block();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);
        Profile testProfile = profileList.get(profileList.size() - 1);
        assertThat(testProfile.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void fullUpdateProfileWithPatch() throws Exception {
        // Initialize the database
        profile.setId(UUID.randomUUID().toString());
        profileRepository.save(profile).block();

        int databaseSizeBeforeUpdate = profileRepository.findAll().collectList().block().size();

        // Update the profile using partial update
        Profile partialUpdatedProfile = new Profile();
        partialUpdatedProfile.setId(profile.getId());

        partialUpdatedProfile.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll().collectList().block();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);
        Profile testProfile = profileList.get(profileList.size() - 1);
        assertThat(testProfile.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingProfile() throws Exception {
        int databaseSizeBeforeUpdate = profileRepository.findAll().collectList().block().size();
        profile.setId(UUID.randomUUID().toString());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, profileDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll().collectList().block();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Profile in Elasticsearch
        verify(mockProfileSearchRepository, times(0)).save(profile);
    }

    @Test
    void patchWithIdMismatchProfile() throws Exception {
        int databaseSizeBeforeUpdate = profileRepository.findAll().collectList().block().size();
        profile.setId(UUID.randomUUID().toString());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll().collectList().block();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Profile in Elasticsearch
        verify(mockProfileSearchRepository, times(0)).save(profile);
    }

    @Test
    void patchWithMissingIdPathParamProfile() throws Exception {
        int databaseSizeBeforeUpdate = profileRepository.findAll().collectList().block().size();
        profile.setId(UUID.randomUUID().toString());

        // Create the Profile
        ProfileDTO profileDTO = profileMapper.toDto(profile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(profileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Profile in the database
        List<Profile> profileList = profileRepository.findAll().collectList().block();
        assertThat(profileList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Profile in Elasticsearch
        verify(mockProfileSearchRepository, times(0)).save(profile);
    }

    @Test
    void deleteProfile() {
        // Configure the mock search repository
        when(mockProfileSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockProfileSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        profile.setId(UUID.randomUUID().toString());
        profileRepository.save(profile).block();

        int databaseSizeBeforeDelete = profileRepository.findAll().collectList().block().size();

        // Delete the profile
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, profile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Profile> profileList = profileRepository.findAll().collectList().block();
        assertThat(profileList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Profile in Elasticsearch
        verify(mockProfileSearchRepository, times(1)).deleteById(profile.getId());
    }

    @Test
    void searchProfile() {
        // Configure the mock search repository
        when(mockProfileSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        profile.setId(UUID.randomUUID().toString());
        profileRepository.save(profile).block();
        when(mockProfileSearchRepository.search("id:" + profile.getId())).thenReturn(Flux.just(profile));

        // Search the profile
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + profile.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(profile.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }
}
