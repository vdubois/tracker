package io.github.vdubois.tracker.web.rest;

import io.github.vdubois.tracker.Application;
import io.github.vdubois.tracker.domain.ProductType;
import io.github.vdubois.tracker.repository.ProductTypeRepository;
import io.github.vdubois.tracker.repository.search.ProductTypeSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ProductTypeResource REST controller.
 *
 * @see ProductTypeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ProductTypeResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    @Inject
    private ProductTypeRepository productTypeRepository;

    @Inject
    private ProductTypeSearchRepository productTypeSearchRepository;

    private MockMvc restProductTypeMockMvc;

    private ProductType productType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProductTypeResource productTypeResource = new ProductTypeResource();
        ReflectionTestUtils.setField(productTypeResource, "productTypeRepository", productTypeRepository);
        ReflectionTestUtils.setField(productTypeResource, "productTypeSearchRepository", productTypeSearchRepository);
        this.restProductTypeMockMvc = MockMvcBuilders.standaloneSetup(productTypeResource).build();
    }

    @Before
    public void initTest() {
        productType = new ProductType();
        productType.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createProductType() throws Exception {
        int databaseSizeBeforeCreate = productTypeRepository.findAll().size();

        // Create the ProductType
        restProductTypeMockMvc.perform(post("/api/productTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(productType)))
                .andExpect(status().isCreated());

        // Validate the ProductType in the database
        List<ProductType> productTypes = productTypeRepository.findAll();
        assertThat(productTypes).hasSize(databaseSizeBeforeCreate + 1);
        ProductType testProductType = productTypes.get(productTypes.size() - 1);
        assertThat(testProductType.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(productTypeRepository.findAll()).hasSize(0);
        // set the field null
        productType.setName(null);

        // Create the ProductType, which fails.
        restProductTypeMockMvc.perform(post("/api/productTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(productType)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<ProductType> productTypes = productTypeRepository.findAll();
        assertThat(productTypes).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllProductTypes() throws Exception {
        // Initialize the database
        productTypeRepository.saveAndFlush(productType);

        // Get all the productTypes
        restProductTypeMockMvc.perform(get("/api/productTypes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(productType.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getProductType() throws Exception {
        // Initialize the database
        productTypeRepository.saveAndFlush(productType);

        // Get the productType
        restProductTypeMockMvc.perform(get("/api/productTypes/{id}", productType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(productType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProductType() throws Exception {
        // Get the productType
        restProductTypeMockMvc.perform(get("/api/productTypes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProductType() throws Exception {
        // Initialize the database
        productTypeRepository.saveAndFlush(productType);

		int databaseSizeBeforeUpdate = productTypeRepository.findAll().size();

        // Update the productType
        productType.setName(UPDATED_NAME);
        restProductTypeMockMvc.perform(put("/api/productTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(productType)))
                .andExpect(status().isOk());

        // Validate the ProductType in the database
        List<ProductType> productTypes = productTypeRepository.findAll();
        assertThat(productTypes).hasSize(databaseSizeBeforeUpdate);
        ProductType testProductType = productTypes.get(productTypes.size() - 1);
        assertThat(testProductType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void deleteProductType() throws Exception {
        // Initialize the database
        productTypeRepository.saveAndFlush(productType);

		int databaseSizeBeforeDelete = productTypeRepository.findAll().size();

        // Get the productType
        restProductTypeMockMvc.perform(delete("/api/productTypes/{id}", productType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ProductType> productTypes = productTypeRepository.findAll();
        assertThat(productTypes).hasSize(databaseSizeBeforeDelete - 1);
    }
}
