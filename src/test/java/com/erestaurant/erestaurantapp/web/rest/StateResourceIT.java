package com.erestaurant.erestaurantapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.erestaurant.erestaurantapp.IntegrationTest;
import com.erestaurant.erestaurantapp.domain.State;
import com.erestaurant.erestaurantapp.repository.EntityManager;
import com.erestaurant.erestaurantapp.repository.StateRepository;
import com.erestaurant.erestaurantapp.repository.search.StateSearchRepository;
import com.erestaurant.erestaurantapp.service.dto.StateDTO;
import com.erestaurant.erestaurantapp.service.mapper.StateMapper;
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
 * Integration tests for the {@link StateResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class StateResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/states";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/states";

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private StateMapper stateMapper;

    /**
     * This repository is mocked in the com.erestaurant.erestaurantapp.repository.search test package.
     *
     * @see com.erestaurant.erestaurantapp.repository.search.StateSearchRepositoryMockConfiguration
     */
    @Autowired
    private StateSearchRepository mockStateSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private State state;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static State createEntity(EntityManager em) {
        State state = new State().description(DEFAULT_DESCRIPTION);
        return state;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static State createUpdatedEntity(EntityManager em) {
        State state = new State().description(UPDATED_DESCRIPTION);
        return state;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(State.class).block();
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
        state = createEntity(em);
    }

    @Test
    void createState() throws Exception {
        int databaseSizeBeforeCreate = stateRepository.findAll().collectList().block().size();
        // Configure the mock search repository
        when(mockStateSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Create the State
        StateDTO stateDTO = stateMapper.toDto(state);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stateDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll().collectList().block();
        assertThat(stateList).hasSize(databaseSizeBeforeCreate + 1);
        State testState = stateList.get(stateList.size() - 1);
        assertThat(testState.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the State in Elasticsearch
        verify(mockStateSearchRepository, times(1)).save(testState);
    }

    @Test
    void createStateWithExistingId() throws Exception {
        // Create the State with an existing ID
        state.setId("existing_id");
        StateDTO stateDTO = stateMapper.toDto(state);

        int databaseSizeBeforeCreate = stateRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll().collectList().block();
        assertThat(stateList).hasSize(databaseSizeBeforeCreate);

        // Validate the State in Elasticsearch
        verify(mockStateSearchRepository, times(0)).save(state);
    }

    @Test
    void getAllStatesAsStream() {
        // Initialize the database
        state.setId(UUID.randomUUID().toString());
        stateRepository.save(state).block();

        List<State> stateList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(StateDTO.class)
            .getResponseBody()
            .map(stateMapper::toEntity)
            .filter(state::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(stateList).isNotNull();
        assertThat(stateList).hasSize(1);
        State testState = stateList.get(0);
        assertThat(testState.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllStates() {
        // Initialize the database
        state.setId(UUID.randomUUID().toString());
        stateRepository.save(state).block();

        // Get all the stateList
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
            .value(hasItem(state.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getState() {
        // Initialize the database
        state.setId(UUID.randomUUID().toString());
        stateRepository.save(state).block();

        // Get the state
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, state.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(state.getId()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    void getNonExistingState() {
        // Get the state
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewState() throws Exception {
        // Configure the mock search repository
        when(mockStateSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        state.setId(UUID.randomUUID().toString());
        stateRepository.save(state).block();

        int databaseSizeBeforeUpdate = stateRepository.findAll().collectList().block().size();

        // Update the state
        State updatedState = stateRepository.findById(state.getId()).block();
        updatedState.description(UPDATED_DESCRIPTION);
        StateDTO stateDTO = stateMapper.toDto(updatedState);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, stateDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stateDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll().collectList().block();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);
        State testState = stateList.get(stateList.size() - 1);
        assertThat(testState.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the State in Elasticsearch
        verify(mockStateSearchRepository).save(testState);
    }

    @Test
    void putNonExistingState() throws Exception {
        int databaseSizeBeforeUpdate = stateRepository.findAll().collectList().block().size();
        state.setId(UUID.randomUUID().toString());

        // Create the State
        StateDTO stateDTO = stateMapper.toDto(state);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, stateDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll().collectList().block();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the State in Elasticsearch
        verify(mockStateSearchRepository, times(0)).save(state);
    }

    @Test
    void putWithIdMismatchState() throws Exception {
        int databaseSizeBeforeUpdate = stateRepository.findAll().collectList().block().size();
        state.setId(UUID.randomUUID().toString());

        // Create the State
        StateDTO stateDTO = stateMapper.toDto(state);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll().collectList().block();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the State in Elasticsearch
        verify(mockStateSearchRepository, times(0)).save(state);
    }

    @Test
    void putWithMissingIdPathParamState() throws Exception {
        int databaseSizeBeforeUpdate = stateRepository.findAll().collectList().block().size();
        state.setId(UUID.randomUUID().toString());

        // Create the State
        StateDTO stateDTO = stateMapper.toDto(state);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stateDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll().collectList().block();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the State in Elasticsearch
        verify(mockStateSearchRepository, times(0)).save(state);
    }

    @Test
    void partialUpdateStateWithPatch() throws Exception {
        // Initialize the database
        state.setId(UUID.randomUUID().toString());
        stateRepository.save(state).block();

        int databaseSizeBeforeUpdate = stateRepository.findAll().collectList().block().size();

        // Update the state using partial update
        State partialUpdatedState = new State();
        partialUpdatedState.setId(state.getId());

        partialUpdatedState.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedState.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedState))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll().collectList().block();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);
        State testState = stateList.get(stateList.size() - 1);
        assertThat(testState.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void fullUpdateStateWithPatch() throws Exception {
        // Initialize the database
        state.setId(UUID.randomUUID().toString());
        stateRepository.save(state).block();

        int databaseSizeBeforeUpdate = stateRepository.findAll().collectList().block().size();

        // Update the state using partial update
        State partialUpdatedState = new State();
        partialUpdatedState.setId(state.getId());

        partialUpdatedState.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedState.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedState))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll().collectList().block();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);
        State testState = stateList.get(stateList.size() - 1);
        assertThat(testState.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingState() throws Exception {
        int databaseSizeBeforeUpdate = stateRepository.findAll().collectList().block().size();
        state.setId(UUID.randomUUID().toString());

        // Create the State
        StateDTO stateDTO = stateMapper.toDto(state);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, stateDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(stateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll().collectList().block();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the State in Elasticsearch
        verify(mockStateSearchRepository, times(0)).save(state);
    }

    @Test
    void patchWithIdMismatchState() throws Exception {
        int databaseSizeBeforeUpdate = stateRepository.findAll().collectList().block().size();
        state.setId(UUID.randomUUID().toString());

        // Create the State
        StateDTO stateDTO = stateMapper.toDto(state);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(stateDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll().collectList().block();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the State in Elasticsearch
        verify(mockStateSearchRepository, times(0)).save(state);
    }

    @Test
    void patchWithMissingIdPathParamState() throws Exception {
        int databaseSizeBeforeUpdate = stateRepository.findAll().collectList().block().size();
        state.setId(UUID.randomUUID().toString());

        // Create the State
        StateDTO stateDTO = stateMapper.toDto(state);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(stateDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the State in the database
        List<State> stateList = stateRepository.findAll().collectList().block();
        assertThat(stateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the State in Elasticsearch
        verify(mockStateSearchRepository, times(0)).save(state);
    }

    @Test
    void deleteState() {
        // Configure the mock search repository
        when(mockStateSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(mockStateSearchRepository.deleteById(anyString())).thenReturn(Mono.empty());
        // Initialize the database
        state.setId(UUID.randomUUID().toString());
        stateRepository.save(state).block();

        int databaseSizeBeforeDelete = stateRepository.findAll().collectList().block().size();

        // Delete the state
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, state.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<State> stateList = stateRepository.findAll().collectList().block();
        assertThat(stateList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the State in Elasticsearch
        verify(mockStateSearchRepository, times(1)).deleteById(state.getId());
    }

    @Test
    void searchState() {
        // Configure the mock search repository
        when(mockStateSearchRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        // Initialize the database
        state.setId(UUID.randomUUID().toString());
        stateRepository.save(state).block();
        when(mockStateSearchRepository.search("id:" + state.getId())).thenReturn(Flux.just(state));

        // Search the state
        webTestClient
            .get()
            .uri(ENTITY_SEARCH_API_URL + "?query=id:" + state.getId())
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(state.getId()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()));
    }
}
