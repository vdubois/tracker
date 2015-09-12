package io.github.vdubois.tracker.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.vdubois.tracker.domain.Brand;
import io.github.vdubois.tracker.repository.BrandRepository;
import io.github.vdubois.tracker.service.UserService;
import io.github.vdubois.tracker.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Brand.
 */
@RestController
@RequestMapping("/api")
public class BrandResource {

    private final Logger log = LoggerFactory.getLogger(BrandResource.class);

    @Inject
    private BrandRepository brandRepository;

    @Inject
    private UserService userService;
    
    /**
     * POST  /brands -> Create a new brand.
     */
    @RequestMapping(value = "/brands",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Brand brand) throws URISyntaxException {
        log.debug("REST request to save Brand : {}", brand);
        if (brand.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new brand cannot already have an ID").build();
        }
        brand.setUser(userService.getUserWithAuthorities());
        brandRepository.save(brand);
        return ResponseEntity.created(new URI("/api/brands/" + brand.getId())).build();
    }

    /**
     * PUT  /brands -> Updates an existing brand.
     */
    @RequestMapping(value = "/brands",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Brand brand) throws URISyntaxException {
        log.debug("REST request to update Brand : {}", brand);
        if (brand.getId() == null) {
            return create(brand);
        }
        brandRepository.save(brand);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /brands -> get all the brands.
     */
    @RequestMapping(value = "/brands",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Brand>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Brand> page = brandRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/brands", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /brands/:id -> get the "id" brand.
     */
    @RequestMapping(value = "/brands/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Brand> get(@PathVariable Long id) {
        log.debug("REST request to get Brand : {}", id);
        return Optional.ofNullable(brandRepository.findOne(id))
            .map(brand -> new ResponseEntity<>(
                brand,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /brands/:id -> delete the "id" brand.
     */
    @RequestMapping(value = "/brands/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Brand : {}", id);
        brandRepository.delete(id);
    }
}
