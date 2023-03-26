package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Activity;
import com.mycompany.myapp.repository.ActivityRepository;
import com.mycompany.myapp.service.criteria.ActivityCriteria;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ActivityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ActivityResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESTINATION = "AAAAAAAAAA";
    private static final String UPDATED_DESTINATION = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_PUBLISHED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_PUBLISHED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_PUBLISHED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final ZonedDateTime DEFAULT_BOOKED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_BOOKED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_BOOKED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/activities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActivityMockMvc;

    private Activity activity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Activity createEntity(EntityManager em) {
        Activity activity = new Activity()
            .title(DEFAULT_TITLE)
            .destination(DEFAULT_DESTINATION)
            .publishedDate(DEFAULT_PUBLISHED_DATE)
            .bookedDate(DEFAULT_BOOKED_DATE);
        return activity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Activity createUpdatedEntity(EntityManager em) {
        Activity activity = new Activity()
            .title(UPDATED_TITLE)
            .destination(UPDATED_DESTINATION)
            .publishedDate(UPDATED_PUBLISHED_DATE)
            .bookedDate(UPDATED_BOOKED_DATE);
        return activity;
    }

    @BeforeEach
    public void initTest() {
        activity = createEntity(em);
    }

    @Test
    @Transactional
    void createActivity() throws Exception {
        int databaseSizeBeforeCreate = activityRepository.findAll().size();
        // Create the Activity
        restActivityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(activity)))
            .andExpect(status().isCreated());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeCreate + 1);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testActivity.getDestination()).isEqualTo(DEFAULT_DESTINATION);
        assertThat(testActivity.getPublishedDate()).isEqualTo(DEFAULT_PUBLISHED_DATE);
        assertThat(testActivity.getBookedDate()).isEqualTo(DEFAULT_BOOKED_DATE);
    }

    @Test
    @Transactional
    void createActivityWithExistingId() throws Exception {
        // Create the Activity with an existing ID
        activity.setId(1L);

        int databaseSizeBeforeCreate = activityRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restActivityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(activity)))
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = activityRepository.findAll().size();
        // set the field null
        activity.setTitle(null);

        // Create the Activity, which fails.

        restActivityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(activity)))
            .andExpect(status().isBadRequest());

        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPublishedDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = activityRepository.findAll().size();
        // set the field null
        activity.setPublishedDate(null);

        // Create the Activity, which fails.

        restActivityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(activity)))
            .andExpect(status().isBadRequest());

        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllActivities() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList
        restActivityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].destination").value(hasItem(DEFAULT_DESTINATION)))
            .andExpect(jsonPath("$.[*].publishedDate").value(hasItem(sameInstant(DEFAULT_PUBLISHED_DATE))))
            .andExpect(jsonPath("$.[*].bookedDate").value(hasItem(sameInstant(DEFAULT_BOOKED_DATE))));
    }

    @Test
    @Transactional
    void getActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get the activity
        restActivityMockMvc
            .perform(get(ENTITY_API_URL_ID, activity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(activity.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.destination").value(DEFAULT_DESTINATION))
            .andExpect(jsonPath("$.publishedDate").value(sameInstant(DEFAULT_PUBLISHED_DATE)))
            .andExpect(jsonPath("$.bookedDate").value(sameInstant(DEFAULT_BOOKED_DATE)));
    }

    @Test
    @Transactional
    void getActivitiesByIdFiltering() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        Long id = activity.getId();

        defaultActivityShouldBeFound("id.equals=" + id);
        defaultActivityShouldNotBeFound("id.notEquals=" + id);

        defaultActivityShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultActivityShouldNotBeFound("id.greaterThan=" + id);

        defaultActivityShouldBeFound("id.lessThanOrEqual=" + id);
        defaultActivityShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllActivitiesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where title equals to DEFAULT_TITLE
        defaultActivityShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the activityList where title equals to UPDATED_TITLE
        defaultActivityShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllActivitiesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultActivityShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the activityList where title equals to UPDATED_TITLE
        defaultActivityShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllActivitiesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where title is not null
        defaultActivityShouldBeFound("title.specified=true");

        // Get all the activityList where title is null
        defaultActivityShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllActivitiesByTitleContainsSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where title contains DEFAULT_TITLE
        defaultActivityShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the activityList where title contains UPDATED_TITLE
        defaultActivityShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllActivitiesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where title does not contain DEFAULT_TITLE
        defaultActivityShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the activityList where title does not contain UPDATED_TITLE
        defaultActivityShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllActivitiesByDestinationIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where destination equals to DEFAULT_DESTINATION
        defaultActivityShouldBeFound("destination.equals=" + DEFAULT_DESTINATION);

        // Get all the activityList where destination equals to UPDATED_DESTINATION
        defaultActivityShouldNotBeFound("destination.equals=" + UPDATED_DESTINATION);
    }

    @Test
    @Transactional
    void getAllActivitiesByDestinationIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where destination in DEFAULT_DESTINATION or UPDATED_DESTINATION
        defaultActivityShouldBeFound("destination.in=" + DEFAULT_DESTINATION + "," + UPDATED_DESTINATION);

        // Get all the activityList where destination equals to UPDATED_DESTINATION
        defaultActivityShouldNotBeFound("destination.in=" + UPDATED_DESTINATION);
    }

    @Test
    @Transactional
    void getAllActivitiesByDestinationIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where destination is not null
        defaultActivityShouldBeFound("destination.specified=true");

        // Get all the activityList where destination is null
        defaultActivityShouldNotBeFound("destination.specified=false");
    }

    @Test
    @Transactional
    void getAllActivitiesByDestinationContainsSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where destination contains DEFAULT_DESTINATION
        defaultActivityShouldBeFound("destination.contains=" + DEFAULT_DESTINATION);

        // Get all the activityList where destination contains UPDATED_DESTINATION
        defaultActivityShouldNotBeFound("destination.contains=" + UPDATED_DESTINATION);
    }

    @Test
    @Transactional
    void getAllActivitiesByDestinationNotContainsSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where destination does not contain DEFAULT_DESTINATION
        defaultActivityShouldNotBeFound("destination.doesNotContain=" + DEFAULT_DESTINATION);

        // Get all the activityList where destination does not contain UPDATED_DESTINATION
        defaultActivityShouldBeFound("destination.doesNotContain=" + UPDATED_DESTINATION);
    }

    @Test
    @Transactional
    void getAllActivitiesByPublishedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where publishedDate equals to DEFAULT_PUBLISHED_DATE
        defaultActivityShouldBeFound("publishedDate.equals=" + DEFAULT_PUBLISHED_DATE);

        // Get all the activityList where publishedDate equals to UPDATED_PUBLISHED_DATE
        defaultActivityShouldNotBeFound("publishedDate.equals=" + UPDATED_PUBLISHED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByPublishedDateIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where publishedDate in DEFAULT_PUBLISHED_DATE or UPDATED_PUBLISHED_DATE
        defaultActivityShouldBeFound("publishedDate.in=" + DEFAULT_PUBLISHED_DATE + "," + UPDATED_PUBLISHED_DATE);

        // Get all the activityList where publishedDate equals to UPDATED_PUBLISHED_DATE
        defaultActivityShouldNotBeFound("publishedDate.in=" + UPDATED_PUBLISHED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByPublishedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where publishedDate is not null
        defaultActivityShouldBeFound("publishedDate.specified=true");

        // Get all the activityList where publishedDate is null
        defaultActivityShouldNotBeFound("publishedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllActivitiesByPublishedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where publishedDate is greater than or equal to DEFAULT_PUBLISHED_DATE
        defaultActivityShouldBeFound("publishedDate.greaterThanOrEqual=" + DEFAULT_PUBLISHED_DATE);

        // Get all the activityList where publishedDate is greater than or equal to UPDATED_PUBLISHED_DATE
        defaultActivityShouldNotBeFound("publishedDate.greaterThanOrEqual=" + UPDATED_PUBLISHED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByPublishedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where publishedDate is less than or equal to DEFAULT_PUBLISHED_DATE
        defaultActivityShouldBeFound("publishedDate.lessThanOrEqual=" + DEFAULT_PUBLISHED_DATE);

        // Get all the activityList where publishedDate is less than or equal to SMALLER_PUBLISHED_DATE
        defaultActivityShouldNotBeFound("publishedDate.lessThanOrEqual=" + SMALLER_PUBLISHED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByPublishedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where publishedDate is less than DEFAULT_PUBLISHED_DATE
        defaultActivityShouldNotBeFound("publishedDate.lessThan=" + DEFAULT_PUBLISHED_DATE);

        // Get all the activityList where publishedDate is less than UPDATED_PUBLISHED_DATE
        defaultActivityShouldBeFound("publishedDate.lessThan=" + UPDATED_PUBLISHED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByPublishedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where publishedDate is greater than DEFAULT_PUBLISHED_DATE
        defaultActivityShouldNotBeFound("publishedDate.greaterThan=" + DEFAULT_PUBLISHED_DATE);

        // Get all the activityList where publishedDate is greater than SMALLER_PUBLISHED_DATE
        defaultActivityShouldBeFound("publishedDate.greaterThan=" + SMALLER_PUBLISHED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByBookedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where bookedDate equals to DEFAULT_BOOKED_DATE
        defaultActivityShouldBeFound("bookedDate.equals=" + DEFAULT_BOOKED_DATE);

        // Get all the activityList where bookedDate equals to UPDATED_BOOKED_DATE
        defaultActivityShouldNotBeFound("bookedDate.equals=" + UPDATED_BOOKED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByBookedDateIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where bookedDate in DEFAULT_BOOKED_DATE or UPDATED_BOOKED_DATE
        defaultActivityShouldBeFound("bookedDate.in=" + DEFAULT_BOOKED_DATE + "," + UPDATED_BOOKED_DATE);

        // Get all the activityList where bookedDate equals to UPDATED_BOOKED_DATE
        defaultActivityShouldNotBeFound("bookedDate.in=" + UPDATED_BOOKED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByBookedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where bookedDate is not null
        defaultActivityShouldBeFound("bookedDate.specified=true");

        // Get all the activityList where bookedDate is null
        defaultActivityShouldNotBeFound("bookedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllActivitiesByBookedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where bookedDate is greater than or equal to DEFAULT_BOOKED_DATE
        defaultActivityShouldBeFound("bookedDate.greaterThanOrEqual=" + DEFAULT_BOOKED_DATE);

        // Get all the activityList where bookedDate is greater than or equal to UPDATED_BOOKED_DATE
        defaultActivityShouldNotBeFound("bookedDate.greaterThanOrEqual=" + UPDATED_BOOKED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByBookedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where bookedDate is less than or equal to DEFAULT_BOOKED_DATE
        defaultActivityShouldBeFound("bookedDate.lessThanOrEqual=" + DEFAULT_BOOKED_DATE);

        // Get all the activityList where bookedDate is less than or equal to SMALLER_BOOKED_DATE
        defaultActivityShouldNotBeFound("bookedDate.lessThanOrEqual=" + SMALLER_BOOKED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByBookedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where bookedDate is less than DEFAULT_BOOKED_DATE
        defaultActivityShouldNotBeFound("bookedDate.lessThan=" + DEFAULT_BOOKED_DATE);

        // Get all the activityList where bookedDate is less than UPDATED_BOOKED_DATE
        defaultActivityShouldBeFound("bookedDate.lessThan=" + UPDATED_BOOKED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByBookedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where bookedDate is greater than DEFAULT_BOOKED_DATE
        defaultActivityShouldNotBeFound("bookedDate.greaterThan=" + DEFAULT_BOOKED_DATE);

        // Get all the activityList where bookedDate is greater than SMALLER_BOOKED_DATE
        defaultActivityShouldBeFound("bookedDate.greaterThan=" + SMALLER_BOOKED_DATE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultActivityShouldBeFound(String filter) throws Exception {
        restActivityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].destination").value(hasItem(DEFAULT_DESTINATION)))
            .andExpect(jsonPath("$.[*].publishedDate").value(hasItem(sameInstant(DEFAULT_PUBLISHED_DATE))))
            .andExpect(jsonPath("$.[*].bookedDate").value(hasItem(sameInstant(DEFAULT_BOOKED_DATE))));

        // Check, that the count call also returns 1
        restActivityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultActivityShouldNotBeFound(String filter) throws Exception {
        restActivityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restActivityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingActivity() throws Exception {
        // Get the activity
        restActivityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Update the activity
        Activity updatedActivity = activityRepository.findById(activity.getId()).get();
        // Disconnect from session so that the updates on updatedActivity are not directly saved in db
        em.detach(updatedActivity);
        updatedActivity
            .title(UPDATED_TITLE)
            .destination(UPDATED_DESTINATION)
            .publishedDate(UPDATED_PUBLISHED_DATE)
            .bookedDate(UPDATED_BOOKED_DATE);

        restActivityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedActivity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedActivity))
            )
            .andExpect(status().isOk());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testActivity.getDestination()).isEqualTo(UPDATED_DESTINATION);
        assertThat(testActivity.getPublishedDate()).isEqualTo(UPDATED_PUBLISHED_DATE);
        assertThat(testActivity.getBookedDate()).isEqualTo(UPDATED_BOOKED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, activity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(activity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateActivityWithPatch() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Update the activity using partial update
        Activity partialUpdatedActivity = new Activity();
        partialUpdatedActivity.setId(activity.getId());

        partialUpdatedActivity.title(UPDATED_TITLE).bookedDate(UPDATED_BOOKED_DATE);

        restActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActivity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActivity))
            )
            .andExpect(status().isOk());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testActivity.getDestination()).isEqualTo(DEFAULT_DESTINATION);
        assertThat(testActivity.getPublishedDate()).isEqualTo(DEFAULT_PUBLISHED_DATE);
        assertThat(testActivity.getBookedDate()).isEqualTo(UPDATED_BOOKED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateActivityWithPatch() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Update the activity using partial update
        Activity partialUpdatedActivity = new Activity();
        partialUpdatedActivity.setId(activity.getId());

        partialUpdatedActivity
            .title(UPDATED_TITLE)
            .destination(UPDATED_DESTINATION)
            .publishedDate(UPDATED_PUBLISHED_DATE)
            .bookedDate(UPDATED_BOOKED_DATE);

        restActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActivity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActivity))
            )
            .andExpect(status().isOk());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testActivity.getDestination()).isEqualTo(UPDATED_DESTINATION);
        assertThat(testActivity.getPublishedDate()).isEqualTo(UPDATED_PUBLISHED_DATE);
        assertThat(testActivity.getBookedDate()).isEqualTo(UPDATED_BOOKED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, activity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(activity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(activity))
            )
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(activity)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        int databaseSizeBeforeDelete = activityRepository.findAll().size();

        // Delete the activity
        restActivityMockMvc
            .perform(delete(ENTITY_API_URL_ID, activity.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
