package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.Profile;
import com.erestaurant.erestaurantapp.repository.ProfileRepository;
import com.erestaurant.erestaurantapp.repository.search.ProfileSearchRepository;
import com.erestaurant.erestaurantapp.service.ProfileService;
import com.erestaurant.erestaurantapp.service.dto.ProfileDTO;
import com.erestaurant.erestaurantapp.service.mapper.ProfileMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Profile}.
 */
@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private final Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private final ProfileRepository profileRepository;

    private final ProfileMapper profileMapper;

    private final ProfileSearchRepository profileSearchRepository;

    public ProfileServiceImpl(
        ProfileRepository profileRepository,
        ProfileMapper profileMapper,
        ProfileSearchRepository profileSearchRepository
    ) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
        this.profileSearchRepository = profileSearchRepository;
    }

    @Override
    public Mono<ProfileDTO> save(ProfileDTO profileDTO) {
        log.debug("Request to save Profile : {}", profileDTO);
        return profileRepository.save(profileMapper.toEntity(profileDTO)).flatMap(profileSearchRepository::save).map(profileMapper::toDto);
    }

    @Override
    public Mono<ProfileDTO> update(ProfileDTO profileDTO) {
        log.debug("Request to save Profile : {}", profileDTO);
        return profileRepository
            .save(profileMapper.toEntity(profileDTO).setIsPersisted())
            .flatMap(profileSearchRepository::save)
            .map(profileMapper::toDto);
    }

    @Override
    public Mono<ProfileDTO> partialUpdate(ProfileDTO profileDTO) {
        log.debug("Request to partially update Profile : {}", profileDTO);

        return profileRepository
            .findById(profileDTO.getId())
            .map(existingProfile -> {
                profileMapper.partialUpdate(existingProfile, profileDTO);

                return existingProfile;
            })
            .flatMap(profileRepository::save)
            .flatMap(savedProfile -> {
                profileSearchRepository.save(savedProfile);

                return Mono.just(savedProfile);
            })
            .map(profileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProfileDTO> findAll() {
        log.debug("Request to get all Profiles");
        return profileRepository.findAll().map(profileMapper::toDto);
    }

    public Mono<Long> countAll() {
        return profileRepository.count();
    }

    public Mono<Long> searchCount() {
        return profileSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProfileDTO> findOne(String id) {
        log.debug("Request to get Profile : {}", id);
        return profileRepository.findById(id).map(profileMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete Profile : {}", id);
        return profileRepository.deleteById(id).then(profileSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProfileDTO> search(String query) {
        log.debug("Request to search Profiles for query {}", query);
        return profileSearchRepository.search(query).map(profileMapper::toDto);
    }
}
