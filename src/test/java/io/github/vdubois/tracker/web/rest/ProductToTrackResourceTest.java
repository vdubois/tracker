package io.github.vdubois.tracker.web.rest;

import io.github.vdubois.tracker.Application;
import io.github.vdubois.tracker.domain.ProductToTrack;
import io.github.vdubois.tracker.repository.ProductToTrackRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the ProductToTrackResource REST controller.
 *
 * @see ProductToTrackResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ProductToTrackResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_TRACKING_URL = "SAMPLE_TEXT";
    private static final String UPDATED_TRACKING_URL = "UPDATED_TEXT";
    private static final String DEFAULT_TRACKING_DOM_SELECTOR = "SAMPLE_TEXT";
    private static final String UPDATED_TRACKING_DOM_SELECTOR = "UPDATED_TEXT";

    private static final BigDecimal DEFAULT_LAST_KNOWN_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_LAST_KNOWN_PRICE = new BigDecimal(1);

    @Inject
    private ProductToTrackRepository productToTrackRepository;

    private MockMvc restProductToTrackMockMvc;

    private ProductToTrack productToTrack;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProductToTrackResource productToTrackResource = new ProductToTrackResource();
        ReflectionTestUtils.setField(productToTrackResource, "productToTrackRepository", productToTrackRepository);
        this.restProductToTrackMockMvc = MockMvcBuilders.standaloneSetup(productToTrackResource).build();
    }

    @Before
    public void initTest() {
        productToTrack = new ProductToTrack();
        productToTrack.setName(DEFAULT_NAME);
        productToTrack.setTrackingUrl(DEFAULT_TRACKING_URL);
        productToTrack.setTrackingDomSelector(DEFAULT_TRACKING_DOM_SELECTOR);
        productToTrack.setLastKnownPrice(DEFAULT_LAST_KNOWN_PRICE);
    }

    @Test
    @Transactional
    public void createProductToTrack() throws Exception {
        int databaseSizeBeforeCreate = productToTrackRepository.findAll().size();

        // Create the ProductToTrack
        restProductToTrackMockMvc.perform(post("/api/productToTracks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(productToTrack)))
                .andExpect(status().isCreated());

        // Validate the ProductToTrack in the database
        List<ProductToTrack> productToTracks = productToTrackRepository.findAll();
        assertThat(productToTracks).hasSize(databaseSizeBeforeCreate + 1);
        ProductToTrack testProductToTrack = productToTracks.get(productToTracks.size() - 1);
        assertThat(testProductToTrack.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProductToTrack.getTrackingUrl()).isEqualTo(DEFAULT_TRACKING_URL);
        assertThat(testProductToTrack.getTrackingDomSelector()).isEqualTo(DEFAULT_TRACKING_DOM_SELECTOR);
        assertThat(testProductToTrack.getLastKnownPrice()).isEqualTo(DEFAULT_LAST_KNOWN_PRICE);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(productToTrackRepository.findAll()).hasSize(0);
        // set the field null
        productToTrack.setName(null);

        // Create the ProductToTrack, which fails.
        restProductToTrackMockMvc.perform(post("/api/productToTracks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(productToTrack)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<ProductToTrack> productToTracks = productToTrackRepository.findAll();
        assertThat(productToTracks).hasSize(0);
    }

    @Test
    @Transactional
    public void checkTrackingUrlIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(productToTrackRepository.findAll()).hasSize(0);
        // set the field null
        productToTrack.setTrackingUrl(null);

        // Create the ProductToTrack, which fails.
        restProductToTrackMockMvc.perform(post("/api/productToTracks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(productToTrack)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<ProductToTrack> productToTracks = productToTrackRepository.findAll();
        assertThat(productToTracks).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllProductToTracks() throws Exception {
        // Initialize the database
        productToTrackRepository.saveAndFlush(productToTrack);

        // Get all the productToTracks
        restProductToTrackMockMvc.perform(get("/api/productToTracks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(productToTrack.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].trackingUrl").value(hasItem(DEFAULT_TRACKING_URL.toString())))
                .andExpect(jsonPath("$.[*].trackingDomSelector").value(hasItem(DEFAULT_TRACKING_DOM_SELECTOR.toString())))
                .andExpect(jsonPath("$.[*].lastKnownPrice").value(hasItem(DEFAULT_LAST_KNOWN_PRICE.intValue())));
    }

    @Test
    @Transactional
    public void getProductToTrack() throws Exception {
        // Initialize the database
        productToTrackRepository.saveAndFlush(productToTrack);

        // Get the productToTrack
        restProductToTrackMockMvc.perform(get("/api/productToTracks/{id}", productToTrack.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(productToTrack.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.trackingUrl").value(DEFAULT_TRACKING_URL.toString()))
            .andExpect(jsonPath("$.trackingDomSelector").value(DEFAULT_TRACKING_DOM_SELECTOR.toString()))
            .andExpect(jsonPath("$.lastKnownPrice").value(DEFAULT_LAST_KNOWN_PRICE.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingProductToTrack() throws Exception {
        // Get the productToTrack
        restProductToTrackMockMvc.perform(get("/api/productToTracks/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProductToTrack() throws Exception {
        // Initialize the database
        productToTrackRepository.saveAndFlush(productToTrack);

		int databaseSizeBeforeUpdate = productToTrackRepository.findAll().size();

        // Update the productToTrack
        productToTrack.setName(UPDATED_NAME);
        productToTrack.setTrackingUrl(UPDATED_TRACKING_URL);
        productToTrack.setTrackingDomSelector(UPDATED_TRACKING_DOM_SELECTOR);
        productToTrack.setLastKnownPrice(UPDATED_LAST_KNOWN_PRICE);
        restProductToTrackMockMvc.perform(put("/api/productToTracks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(productToTrack)))
                .andExpect(status().isOk());

        // Validate the ProductToTrack in the database
        List<ProductToTrack> productToTracks = productToTrackRepository.findAll();
        assertThat(productToTracks).hasSize(databaseSizeBeforeUpdate);
        ProductToTrack testProductToTrack = productToTracks.get(productToTracks.size() - 1);
        assertThat(testProductToTrack.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProductToTrack.getTrackingUrl()).isEqualTo(UPDATED_TRACKING_URL);
        assertThat(testProductToTrack.getTrackingDomSelector()).isEqualTo(UPDATED_TRACKING_DOM_SELECTOR);
        assertThat(testProductToTrack.getLastKnownPrice()).isEqualTo(UPDATED_LAST_KNOWN_PRICE);
    }

    @Test
    @Transactional
    public void deleteProductToTrack() throws Exception {
        // Initialize the database
        productToTrackRepository.saveAndFlush(productToTrack);

		int databaseSizeBeforeDelete = productToTrackRepository.findAll().size();

        // Get the productToTrack
        restProductToTrackMockMvc.perform(delete("/api/productToTracks/{id}", productToTrack.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ProductToTrack> productToTracks = productToTrackRepository.findAll();
        assertThat(productToTracks).hasSize(databaseSizeBeforeDelete - 1);
    }
}
