package io.github.vdubois.tracker.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.vdubois.tracker.domain.ProductToTrack;
import io.github.vdubois.tracker.repository.ProductToTrackRepository;
import io.github.vdubois.tracker.repository.search.ProductToTrackSearchRepository;
import io.github.vdubois.tracker.service.UserService;
import io.github.vdubois.tracker.web.rest.util.PaginationUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing ProductToTrack.
 */
@RestController
@RequestMapping("/api")
public class ProductToTrackResource {

    private final Logger log = LoggerFactory.getLogger(ProductToTrackResource.class);

    @Inject
    private ProductToTrackRepository productToTrackRepository;

    @Inject
    private ProductToTrackSearchRepository productToTrackSearchRepository;

    @Inject
    private UserService userService;
    
    /**
     * POST  /productToTracks -> Create a new productToTrack.
     */
    @RequestMapping(value = "/productToTracks",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody ProductToTrack productToTrack) throws URISyntaxException {
        log.debug("REST request to save ProductToTrack : {}", productToTrack);
        if (productToTrack.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new productToTrack cannot already have an ID").build();
        }
        productToTrack.setUser(userService.getUserWithAuthorities());
        if (StringUtils.isNotEmpty(productToTrack.getTrackingDomSelector())) {
            try {
                Document doc = Jsoup.connect(productToTrack.getTrackingUrl()).get();
                Elements priceElements = doc.select(productToTrack.getTrackingDomSelector());
                String priceAsText = priceElements.get(0).text();
                priceAsText = priceAsText.replaceAll("€", ".");
                productToTrack.setLastKnownPrice(new BigDecimal(priceAsText));
            } catch (IOException ioException) {
                log.error(ioException.getMessage(), ioException);
            }
        }
        productToTrackRepository.save(productToTrack);
        productToTrackSearchRepository.save(productToTrack);
        return ResponseEntity.created(new URI("/api/productToTracks/" + productToTrack.getId())).build();
    }

    /**
     * PUT  /productToTracks -> Updates an existing productToTrack.
     */
    @RequestMapping(value = "/productToTracks",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody ProductToTrack productToTrack) throws URISyntaxException {
        log.debug("REST request to update ProductToTrack : {}", productToTrack);
        if (productToTrack.getId() == null) {
            return create(productToTrack);
        }
        productToTrackRepository.save(productToTrack);
        productToTrackSearchRepository.save(productToTrack);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /productToTracks -> get all the productToTracks.
     */
    @RequestMapping(value = "/productToTracks",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ProductToTrack>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<ProductToTrack> page = productToTrackRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/productToTracks", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /productToTracks/:id -> get the "id" productToTrack.
     */
    @RequestMapping(value = "/productToTracks/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProductToTrack> get(@PathVariable Long id) {
        log.debug("REST request to get ProductToTrack : {}", id);
        return Optional.ofNullable(productToTrackRepository.findOne(id))
            .map(productToTrack -> new ResponseEntity<>(
                productToTrack,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /productToTracks/:id -> delete the "id" productToTrack.
     */
    @RequestMapping(value = "/productToTracks/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete ProductToTrack : {}", id);
        productToTrackRepository.delete(id);
        productToTrackSearchRepository.delete(id);
    }

    /**
     * SEARCH  /_search/productToTracks/:query -> search for the productToTrack corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/productToTracks/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ProductToTrack> search(@PathVariable String query) {
        return StreamSupport
            .stream(productToTrackSearchRepository.search(queryString(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
