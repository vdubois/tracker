package io.github.vdubois.tracker.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.vdubois.tracker.domain.Price;
import io.github.vdubois.tracker.repository.PriceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing Price.
 */
@RestController
@RequestMapping("/api")
public class PriceResource {

    private final Logger log = LoggerFactory.getLogger(PriceResource.class);

    @Inject
    private PriceRepository priceRepository;

    /**
     * POST  /prices -> Create a new price.
     */
    @RequestMapping(value = "/prices",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Price price) throws URISyntaxException {
        log.debug("REST request to save Price : {}", price);
        if (price.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new price cannot already have an ID").build();
        }
        priceRepository.save(price);
        return ResponseEntity.created(new URI("/api/prices/" + price.getId())).build();
    }

    /**
     * PUT  /prices -> Updates an existing price.
     */
    @RequestMapping(value = "/prices",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Price price) throws URISyntaxException {
        log.debug("REST request to update Price : {}", price);
        if (price.getId() == null) {
            return create(price);
        }
        priceRepository.save(price);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /prices -> get all the prices.
     */
    @RequestMapping(value = "/prices",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Price>> getAll(@RequestParam(value = "page", required = false) Integer offset,
                                              @RequestParam(value = "per_page", required = false) Integer limit)
            throws URISyntaxException {
        List<Price> prices = priceRepository.findAllForCurrentUser();
        return new ResponseEntity<>(prices, HttpStatus.OK);
    }

    /**
     * GET  /prices/:id -> get the "id" price.
     */
    @RequestMapping(value = "/prices/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Price> get(@PathVariable Long id) {
        log.debug("REST request to get Price : {}", id);
        return Optional.ofNullable(priceRepository.findOne(id))
            .map(price -> new ResponseEntity<>(
                price,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /prices/:id -> delete the "id" price.
     */
    @RequestMapping(value = "/prices/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Price : {}", id);
        priceRepository.delete(id);
    }
}
