package io.github.vdubois.tracker.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.vdubois.tracker.domain.ProductToTrack;
import io.github.vdubois.tracker.domain.Store;
import io.github.vdubois.tracker.repository.ProductToTrackRepository;
import io.github.vdubois.tracker.repository.StoreRepository;
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
 * REST controller for managing Store.
 */
@RestController
@RequestMapping("/api")
public class StoreResource {

    private final Logger log = LoggerFactory.getLogger(StoreResource.class);

    @Inject
    private StoreRepository storeRepository;

    @Inject
    private UserService userService;

    @Inject
    private ProductToTrackRepository productToTrackRepository;
    
    /**
     * POST  /stores -> Create a new store.
     */
    @RequestMapping(value = "/stores",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Store store) throws URISyntaxException {
        log.debug("REST request to save Store : {}", store);
        if (store.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new store cannot already have an ID").build();
        }
        store.setUser(userService.getUserWithAuthorities());
        storeRepository.save(store);
        return ResponseEntity.created(new URI("/api/stores/" + store.getId())).build();
    }

    /**
     * PUT  /stores -> Updates an existing store.
     */
    @RequestMapping(value = "/stores",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Store store) throws URISyntaxException {
        log.debug("REST request to update Store : {}", store);
        if (store.getId() == null) {
            return create(store);
        }
        storeRepository.save(store);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /stores -> get all the stores.
     */
    @RequestMapping(value = "/stores",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Store>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Store> page = storeRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/stores", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /stores/:id -> get the "id" store.
     */
    @RequestMapping(value = "/stores/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Store> get(@PathVariable Long id) {
        log.debug("REST request to get Store : {}", id);
        return Optional.ofNullable(storeRepository.findOne(id))
            .map(store -> new ResponseEntity<>(
                store,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /stores/:id -> delete the "id" store.
     */
    @RequestMapping(value = "/stores/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Store : {}", id);
        storeRepository.delete(id);
    }
}
