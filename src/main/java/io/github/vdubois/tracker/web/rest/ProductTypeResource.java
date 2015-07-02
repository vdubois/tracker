package io.github.vdubois.tracker.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.vdubois.tracker.domain.ProductType;
import io.github.vdubois.tracker.repository.ProductTypeRepository;
import io.github.vdubois.tracker.repository.search.ProductTypeSearchRepository;
import io.github.vdubois.tracker.service.UserService;
import io.github.vdubois.tracker.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ProductType.
 */
@RestController
@RequestMapping("/api")
public class ProductTypeResource {

    private final Logger log = LoggerFactory.getLogger(ProductTypeResource.class);

    @Inject
    private ProductTypeRepository productTypeRepository;

    @Inject
    private ProductTypeSearchRepository productTypeSearchRepository;

    @Inject
    private UserService userService;
    
    /**
     * POST  /productTypes -> Create a new productType.
     */
    @RequestMapping(value = "/productTypes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody ProductType productType) throws URISyntaxException {
        log.debug("REST request to save ProductType : {}", productType);
        if (productType.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new productType cannot already have an ID").build();
        }
        productType.setUser(userService.getUserWithAuthorities());
        productTypeRepository.save(productType);
        productTypeSearchRepository.save(productType);
        return ResponseEntity.created(new URI("/api/productTypes/" + productType.getId())).build();
    }

    /**
     * PUT  /productTypes -> Updates an existing productType.
     */
    @RequestMapping(value = "/productTypes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody ProductType productType) throws URISyntaxException {
        log.debug("REST request to update ProductType : {}", productType);
        if (productType.getId() == null) {
            return create(productType);
        }
        productTypeRepository.save(productType);
        productTypeSearchRepository.save(productType);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /productTypes -> get all the productTypes.
     */
    @RequestMapping(value = "/productTypes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ProductType>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<ProductType> page = productTypeRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/productTypes", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /productTypes/:id -> get the "id" productType.
     */
    @RequestMapping(value = "/productTypes/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProductType> get(@PathVariable Long id) {
        log.debug("REST request to get ProductType : {}", id);
        return Optional.ofNullable(productTypeRepository.findOne(id))
            .map(productType -> new ResponseEntity<>(
                productType,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /productTypes/:id -> delete the "id" productType.
     */
    @RequestMapping(value = "/productTypes/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete ProductType : {}", id);
        productTypeRepository.delete(id);
        productTypeSearchRepository.delete(id);
    }

    /**
     * SEARCH  /_search/productTypes/:query -> search for the productType corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/productTypes/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ProductType> search(@PathVariable String query) {
        return StreamSupport
            .stream(productTypeSearchRepository.search(queryString(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
