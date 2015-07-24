package io.github.vdubois.tracker.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.vdubois.tracker.domain.Point;
import io.github.vdubois.tracker.domain.ProductToTrack;
import io.github.vdubois.tracker.repository.ProductToTrackRepository;
import io.github.vdubois.tracker.service.PriceService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by vdubois on 22/07/15.
 */
@RestController
@RequestMapping("/api")
public class PricesEvolutionResource {

    @Inject
    private PriceService priceService;

    @Inject
    private ProductToTrackRepository productToTrackRepository;
    
    @RequestMapping(value = "/pricesevolution/{productToTrackId}", 
            method = RequestMethod.GET, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Point> find(@PathVariable Long productToTrackId) {
        ProductToTrack productToTrack = productToTrackRepository.findOne(productToTrackId);
        List<Point> graphData = priceService.findAllPricesEvolutionsForProductToTrack(productToTrack);
        return graphData;
    }
}
