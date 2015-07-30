package io.github.vdubois.tracker.web.rest;

import io.github.vdubois.tracker.Application;
import io.github.vdubois.tracker.domain.Alert;
import io.github.vdubois.tracker.repository.AlertRepository;
import io.github.vdubois.tracker.repository.search.AlertSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AlertResource REST controller.
 *
 * @see AlertResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class AlertResourceTest {


    private static final BigDecimal DEFAULT_PRICE_LOWER_THAN = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE_LOWER_THAN = new BigDecimal(1);

    @Inject
    private AlertRepository alertRepository;

    @Inject
    private AlertSearchRepository alertSearchRepository;

    private MockMvc restAlertMockMvc;

    private Alert alert;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AlertResource alertResource = new AlertResource();
        ReflectionTestUtils.setField(alertResource, "alertRepository", alertRepository);
        ReflectionTestUtils.setField(alertResource, "alertSearchRepository", alertSearchRepository);
        this.restAlertMockMvc = MockMvcBuilders.standaloneSetup(alertResource).build();
    }

    @Before
    public void initTest() {
        alert = new Alert();
        alert.setPriceLowerThan(DEFAULT_PRICE_LOWER_THAN);
    }

    @Test
    @Transactional
    public void createAlert() throws Exception {
        int databaseSizeBeforeCreate = alertRepository.findAll().size();

        // Create the Alert
        restAlertMockMvc.perform(post("/api/alerts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(alert)))
                .andExpect(status().isCreated());

        // Validate the Alert in the database
        List<Alert> alerts = alertRepository.findAll();
        assertThat(alerts).hasSize(databaseSizeBeforeCreate + 1);
        Alert testAlert = alerts.get(alerts.size() - 1);
        assertThat(testAlert.getPriceLowerThan()).isEqualTo(DEFAULT_PRICE_LOWER_THAN);
    }

    @Test
    @Transactional
    public void checkPriceLowerThanIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(alertRepository.findAll()).hasSize(0);
        // set the field null
        alert.setPriceLowerThan(null);

        // Create the Alert, which fails.
        restAlertMockMvc.perform(post("/api/alerts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(alert)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Alert> alerts = alertRepository.findAll();
        assertThat(alerts).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllAlerts() throws Exception {
        // Initialize the database
        alertRepository.saveAndFlush(alert);

        // Get all the alerts
        restAlertMockMvc.perform(get("/api/alerts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(alert.getId().intValue())))
                .andExpect(jsonPath("$.[*].priceLowerThan").value(hasItem(DEFAULT_PRICE_LOWER_THAN.intValue())));
    }

    @Test
    @Transactional
    public void getAlert() throws Exception {
        // Initialize the database
        alertRepository.saveAndFlush(alert);

        // Get the alert
        restAlertMockMvc.perform(get("/api/alerts/{id}", alert.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(alert.getId().intValue()))
            .andExpect(jsonPath("$.priceLowerThan").value(DEFAULT_PRICE_LOWER_THAN.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingAlert() throws Exception {
        // Get the alert
        restAlertMockMvc.perform(get("/api/alerts/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAlert() throws Exception {
        // Initialize the database
        alertRepository.saveAndFlush(alert);

		int databaseSizeBeforeUpdate = alertRepository.findAll().size();

        // Update the alert
        alert.setPriceLowerThan(UPDATED_PRICE_LOWER_THAN);
        restAlertMockMvc.perform(put("/api/alerts")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(alert)))
                .andExpect(status().isOk());

        // Validate the Alert in the database
        List<Alert> alerts = alertRepository.findAll();
        assertThat(alerts).hasSize(databaseSizeBeforeUpdate);
        Alert testAlert = alerts.get(alerts.size() - 1);
        assertThat(testAlert.getPriceLowerThan()).isEqualTo(UPDATED_PRICE_LOWER_THAN);
    }

    @Test
    @Transactional
    public void deleteAlert() throws Exception {
        // Initialize the database
        alertRepository.saveAndFlush(alert);

		int databaseSizeBeforeDelete = alertRepository.findAll().size();

        // Get the alert
        restAlertMockMvc.perform(delete("/api/alerts/{id}", alert.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Alert> alerts = alertRepository.findAll();
        assertThat(alerts).hasSize(databaseSizeBeforeDelete - 1);
    }
}
