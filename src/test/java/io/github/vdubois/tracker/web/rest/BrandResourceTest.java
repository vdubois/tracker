package io.github.vdubois.tracker.web.rest;

import io.github.vdubois.tracker.Application;
import io.github.vdubois.tracker.domain.Brand;
import io.github.vdubois.tracker.repository.BrandRepository;
import io.github.vdubois.tracker.repository.search.BrandSearchRepository;

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
 * Test class for the BrandResource REST controller.
 *
 * @see BrandResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class BrandResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    @Inject
    private BrandRepository brandRepository;

    @Inject
    private BrandSearchRepository brandSearchRepository;

    private MockMvc restBrandMockMvc;

    private Brand brand;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BrandResource brandResource = new BrandResource();
        ReflectionTestUtils.setField(brandResource, "brandRepository", brandRepository);
        ReflectionTestUtils.setField(brandResource, "brandSearchRepository", brandSearchRepository);
        this.restBrandMockMvc = MockMvcBuilders.standaloneSetup(brandResource).build();
    }

    @Before
    public void initTest() {
        brand = new Brand();
        brand.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createBrand() throws Exception {
        int databaseSizeBeforeCreate = brandRepository.findAll().size();

        // Create the Brand
        restBrandMockMvc.perform(post("/api/brands")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(brand)))
                .andExpect(status().isCreated());

        // Validate the Brand in the database
        List<Brand> brands = brandRepository.findAll();
        assertThat(brands).hasSize(databaseSizeBeforeCreate + 1);
        Brand testBrand = brands.get(brands.size() - 1);
        assertThat(testBrand.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(brandRepository.findAll()).hasSize(0);
        // set the field null
        brand.setName(null);

        // Create the Brand, which fails.
        restBrandMockMvc.perform(post("/api/brands")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(brand)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Brand> brands = brandRepository.findAll();
        assertThat(brands).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllBrands() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brands
        restBrandMockMvc.perform(get("/api/brands"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(brand.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getBrand() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get the brand
        restBrandMockMvc.perform(get("/api/brands/{id}", brand.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(brand.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBrand() throws Exception {
        // Get the brand
        restBrandMockMvc.perform(get("/api/brands/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBrand() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

		int databaseSizeBeforeUpdate = brandRepository.findAll().size();

        // Update the brand
        brand.setName(UPDATED_NAME);
        restBrandMockMvc.perform(put("/api/brands")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(brand)))
                .andExpect(status().isOk());

        // Validate the Brand in the database
        List<Brand> brands = brandRepository.findAll();
        assertThat(brands).hasSize(databaseSizeBeforeUpdate);
        Brand testBrand = brands.get(brands.size() - 1);
        assertThat(testBrand.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void deleteBrand() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

		int databaseSizeBeforeDelete = brandRepository.findAll().size();

        // Get the brand
        restBrandMockMvc.perform(delete("/api/brands/{id}", brand.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Brand> brands = brandRepository.findAll();
        assertThat(brands).hasSize(databaseSizeBeforeDelete - 1);
    }
}
