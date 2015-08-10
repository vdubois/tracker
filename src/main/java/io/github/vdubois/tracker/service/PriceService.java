package io.github.vdubois.tracker.service;

import io.github.vdubois.tracker.domain.Point;
import io.github.vdubois.tracker.domain.Price;
import io.github.vdubois.tracker.domain.ProductToTrack;
import io.github.vdubois.tracker.repository.PriceRepository;
import io.github.vdubois.tracker.repository.ProductToTrackRepository;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vdubois on 18/07/15.
 */
@Service
@Log
@EnableScheduling
public class PriceService {

    @Inject
    private PriceRepository priceRepository;

    @Inject
    private ProductToTrackRepository productToTrackRepository;

    /**
     * @param productToTrack produit à suivre
     * @return le prix extrait de la page
     * @throws IOException exception d'entrée/sortie
     */
    public BigDecimal extractPriceFromURLWithDOMSelectorIfFilled(ProductToTrack productToTrack) throws IOException {
        if (StringUtils.isNotEmpty(productToTrack.getTrackingDomSelector())) {
            try {
                Document doc = Jsoup.connect(productToTrack.getTrackingUrl()).get();
                Elements priceElements = doc.select(productToTrack.getTrackingDomSelector());
                String priceAsText = priceElements.get(0).text();
                priceAsText = priceAsText.replaceAll("€", ".");
                return new BigDecimal(priceAsText);
            } catch (Exception exception) {
                log.severe(exception.getMessage());
                throw exception;
            }
        }
        return null;
    }

    /**
     * @param productToTrack produit dont le prix est à enregistrer
     */
    public void recordPriceForProductToTrack(ProductToTrack productToTrack) {
        Price recordPrice = new Price();
        recordPrice.setCreatedAt(DateTime.now());
        recordPrice.setProductToTrack(productToTrack);
        recordPrice.setValue(productToTrack.getLastKnownPrice());
        priceRepository.save(recordPrice);
    }

    @Scheduled(cron = "0 0 0/12 * * *")
    @Async
    public void refreshPrices() throws IOException {
        List<ProductToTrack> productsToTrack = productToTrackRepository.findAll();
        for (ProductToTrack productToTrack : productsToTrack) {
            productToTrack.setLastKnownPrice(extractPriceFromURLWithDOMSelectorIfFilled(productToTrack));
            recordPriceForProductToTrack(productToTrack);
        }
    }

    public List<Point> findAllPricesEvolutionsForProductToTrack(ProductToTrack productToTrack) {
        List<Price> pricesForProductToTrack = priceRepository.findAllByProductToTrack(productToTrack);
        List<Point> graphData = pricesForProductToTrack
                .stream()
                .sorted(Comparator.comparing(Price::getCreatedAt))
                .map(price -> {
                    Point point = new Point();
                    point.setDate(price.getCreatedAt().toString());
                    point.setValue(price.getValue().toString());
                    return point;
                })
                .collect(Collectors.toList());
        log.fine(graphData.toString());
        return graphData;
    }
}
