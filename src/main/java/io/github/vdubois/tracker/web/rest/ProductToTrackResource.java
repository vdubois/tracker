package io.github.vdubois.tracker.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.vdubois.tracker.domain.Alert;
import io.github.vdubois.tracker.domain.ProductToTrack;
import io.github.vdubois.tracker.repository.AlertRepository;
import io.github.vdubois.tracker.repository.ProductToTrackRepository;
import io.github.vdubois.tracker.repository.StoreRepository;
import io.github.vdubois.tracker.service.PriceService;
import io.github.vdubois.tracker.service.UserService;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

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
    private UserService userService;

    @Inject
    private PriceService priceService;

    @Inject
    private StoreRepository storeRepository;

    @Inject
    private AlertRepository alertRepository;
    
    /**
     * POST  /productToTracks -> Create a new productToTrack.
     */
    @RequestMapping(value = "/productToTracks",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody ProductToTrack productToTrack) throws URISyntaxException, IOException {
        log.debug("REST request to save ProductToTrack : {}", productToTrack);
        if (productToTrack.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new productToTrack cannot already have an ID").build();
        }
        BigDecimal lastKnownPrice = priceService.extractPriceFromURLWithDOMSelectorIfFilled(productToTrack);
        productToTrack.setUser(userService.getUserWithAuthorities());
        productToTrack.setStore(storeRepository.findOne(productToTrack.getStore().getId()));
        productToTrack.setLastKnownPrice(lastKnownPrice);
        productToTrackRepository.save(productToTrack);
        ProductToTrack productToTrackToUpdate = productToTrackRepository.findOne(productToTrack.getId());
        if (productToTrackToUpdate != null) {
            priceService.recordPriceForProductToTrack(productToTrackToUpdate);
        }
        return ResponseEntity.created(new URI("/api/productToTracks/" + productToTrack.getId())).build();
    }

    /**
     * PUT  /productToTracks -> Updates an existing productToTrack.
     */
    @RequestMapping(value = "/productToTracks",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody ProductToTrack productToTrack) throws URISyntaxException, IOException {
        log.debug("REST request to update ProductToTrack : {}", productToTrack);
        if (productToTrack.getId() == null) {
            return create(productToTrack);
        }
        productToTrack.setLastKnownPrice(
                priceService.extractPriceFromURLWithDOMSelectorIfFilled(productToTrack));
        productToTrack.setStore(storeRepository.findOne(productToTrack.getStore().getId()));
        productToTrackRepository.save(productToTrack);
        priceService.recordPriceForProductToTrack(productToTrack);
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
        List<ProductToTrack> products = productToTrackRepository.findAllForCurrentUser();
        return new ResponseEntity<>(products, HttpStatus.OK);
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
        List<Alert> productAlerts = 
                alertRepository.findAllByProductToTrack(productToTrackRepository.findOne(id));
        productAlerts.forEach(alertRepository::delete);
        productToTrackRepository.delete(id);
    }
}
