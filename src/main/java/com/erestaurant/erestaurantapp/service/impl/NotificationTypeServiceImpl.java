package com.erestaurant.erestaurantapp.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.erestaurant.erestaurantapp.domain.NotificationType;
import com.erestaurant.erestaurantapp.repository.NotificationTypeRepository;
import com.erestaurant.erestaurantapp.repository.search.NotificationTypeSearchRepository;
import com.erestaurant.erestaurantapp.service.NotificationTypeService;
import com.erestaurant.erestaurantapp.service.dto.NotificationTypeDTO;
import com.erestaurant.erestaurantapp.service.mapper.NotificationTypeMapper;
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
 * Service Implementation for managing {@link NotificationType}.
 */
@Service
@Transactional
public class NotificationTypeServiceImpl implements NotificationTypeService {

    private final Logger log = LoggerFactory.getLogger(NotificationTypeServiceImpl.class);

    private final NotificationTypeRepository notificationTypeRepository;

    private final NotificationTypeMapper notificationTypeMapper;

    private final NotificationTypeSearchRepository notificationTypeSearchRepository;

    public NotificationTypeServiceImpl(
        NotificationTypeRepository notificationTypeRepository,
        NotificationTypeMapper notificationTypeMapper,
        NotificationTypeSearchRepository notificationTypeSearchRepository
    ) {
        this.notificationTypeRepository = notificationTypeRepository;
        this.notificationTypeMapper = notificationTypeMapper;
        this.notificationTypeSearchRepository = notificationTypeSearchRepository;
    }

    @Override
    public Mono<NotificationTypeDTO> save(NotificationTypeDTO notificationTypeDTO) {
        log.debug("Request to save NotificationType : {}", notificationTypeDTO);
        return notificationTypeRepository
            .save(notificationTypeMapper.toEntity(notificationTypeDTO))
            .flatMap(notificationTypeSearchRepository::save)
            .map(notificationTypeMapper::toDto);
    }

    @Override
    public Mono<NotificationTypeDTO> update(NotificationTypeDTO notificationTypeDTO) {
        log.debug("Request to save NotificationType : {}", notificationTypeDTO);
        return notificationTypeRepository
            .save(notificationTypeMapper.toEntity(notificationTypeDTO).setIsPersisted())
            .flatMap(notificationTypeSearchRepository::save)
            .map(notificationTypeMapper::toDto);
    }

    @Override
    public Mono<NotificationTypeDTO> partialUpdate(NotificationTypeDTO notificationTypeDTO) {
        log.debug("Request to partially update NotificationType : {}", notificationTypeDTO);

        return notificationTypeRepository
            .findById(notificationTypeDTO.getId())
            .map(existingNotificationType -> {
                notificationTypeMapper.partialUpdate(existingNotificationType, notificationTypeDTO);

                return existingNotificationType;
            })
            .flatMap(notificationTypeRepository::save)
            .flatMap(savedNotificationType -> {
                notificationTypeSearchRepository.save(savedNotificationType);

                return Mono.just(savedNotificationType);
            })
            .map(notificationTypeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<NotificationTypeDTO> findAll() {
        log.debug("Request to get all NotificationTypes");
        return notificationTypeRepository.findAll().map(notificationTypeMapper::toDto);
    }

    public Mono<Long> countAll() {
        return notificationTypeRepository.count();
    }

    public Mono<Long> searchCount() {
        return notificationTypeSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<NotificationTypeDTO> findOne(String id) {
        log.debug("Request to get NotificationType : {}", id);
        return notificationTypeRepository.findById(id).map(notificationTypeMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        log.debug("Request to delete NotificationType : {}", id);
        return notificationTypeRepository.deleteById(id).then(notificationTypeSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<NotificationTypeDTO> search(String query) {
        log.debug("Request to search NotificationTypes for query {}", query);
        return notificationTypeSearchRepository.search(query).map(notificationTypeMapper::toDto);
    }
}
