package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Activity;
import com.mycompany.myapp.repository.ActivityRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Activity}.
 */
@Service
@Transactional
public class ActivityService {

    private final Logger log = LoggerFactory.getLogger(ActivityService.class);

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /**
     * Save a activity.
     *
     * @param activity the entity to save.
     * @return the persisted entity.
     */
    public Activity save(Activity activity) {
        log.debug("Request to save Activity : {}", activity);
        return activityRepository.save(activity);
    }

    /**
     * Update a activity.
     *
     * @param activity the entity to save.
     * @return the persisted entity.
     */
    public Activity update(Activity activity) {
        log.debug("Request to update Activity : {}", activity);
        return activityRepository.save(activity);
    }

    /**
     * Partially update a activity.
     *
     * @param activity the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Activity> partialUpdate(Activity activity) {
        log.debug("Request to partially update Activity : {}", activity);

        return activityRepository
            .findById(activity.getId())
            .map(existingActivity -> {
                if (activity.getTitle() != null) {
                    existingActivity.setTitle(activity.getTitle());
                }
                if (activity.getDestination() != null) {
                    existingActivity.setDestination(activity.getDestination());
                }
                if (activity.getPublishedDate() != null) {
                    existingActivity.setPublishedDate(activity.getPublishedDate());
                }
                if (activity.getBookedDate() != null) {
                    existingActivity.setBookedDate(activity.getBookedDate());
                }

                return existingActivity;
            })
            .map(activityRepository::save);
    }

    /**
     * Get all the activities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Activity> findAll(Pageable pageable) {
        log.debug("Request to get all Activities");
        return activityRepository.findAll(pageable);
    }

    /**
     * Get one activity by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Activity> findOne(Long id) {
        log.debug("Request to get Activity : {}", id);
        return activityRepository.findById(id);
    }

    /**
     * Delete the activity by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Activity : {}", id);
        activityRepository.deleteById(id);
    }
}
