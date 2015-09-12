package io.github.vdubois.tracker.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.vdubois.tracker.domain.Price;
import io.github.vdubois.tracker.repository.PriceRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by vdubois on 22/07/15.
 */
@RestController
@RequestMapping("/api")
public class PricesEvolutionResource {

    @Inject
    private PriceRepository priceRepository;
    
    @RequestMapping(value = "/pricesevolution/{productToTrackName}", 
            method = RequestMethod.GET, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Map<String, List<Price>> find(@PathVariable String productToTrackName) {
        return priceRepository.findAllForCurrentUser()
                .stream()
                .filter(price -> productToTrackName.equals(price.getProductToTrack().getName()))
                .sorted(Comparator.comparing(Price::getCreatedAt))
                .collect(Collectors.groupingBy(Price::getProductToTrackStoreName));
    }
}
