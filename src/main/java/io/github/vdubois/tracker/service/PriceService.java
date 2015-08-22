package io.github.vdubois.tracker.service;

import io.github.vdubois.tracker.domain.Point;
import io.github.vdubois.tracker.domain.Price;
import io.github.vdubois.tracker.domain.ProductToTrack;
import io.github.vdubois.tracker.repository.PriceRepository;
import io.github.vdubois.tracker.repository.ProductToTrackRepository;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
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
        try {
            Document doc = Jsoup.connect(productToTrack.getTrackingUrl()).get();
            String selector = productToTrack.getTrackingDomSelector();
            if (StringUtils.isEmpty(selector)) {
                selector = productToTrack.getStore().getBaseDomSelector();
            }
            Elements priceElements = doc.select(selector);
            log.warning(priceElements.toString());
            String priceAsText = priceElements.get(0).text();
            log.warning(priceAsText);
            priceAsText = priceAsText.replaceAll("€", ".");
            return new BigDecimal(priceAsText);
        } catch (Exception exception) {
            log.severe(exception.getMessage());
            throw exception;
        }
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

    @Scheduled(cron = "0 0 * * * *")
    @Async
    public void refreshPrices() throws IOException {
        List<ProductToTrack> productsToTrack = productToTrackRepository.findAll();
        for (ProductToTrack productToTrack : productsToTrack) {
            if (isLastKnownPriceOlderThanThreeHoursForProductToTrack(productToTrack)) {
                productToTrack.setLastKnownPrice(extractPriceFromURLWithDOMSelectorIfFilled(productToTrack));
                productToTrackRepository.save(productsToTrack);
                recordPriceForProductToTrack(productToTrack);
            }
        }
    }

    private boolean isLastKnownPriceOlderThanThreeHoursForProductToTrack(ProductToTrack productToTrack) {
        // we order prices by last date first
        List<Price> prices = productToTrackRepository.findOne(productToTrack.getId()).getPricess()
                .stream().sorted(Comparator.comparing(Price::getCreatedAt).reversed()).collect(Collectors.toList());
        DateTime now = DateTime.now();
        Hours hours = Hours.hoursBetween(prices.get(0).getCreatedAt(), now);
        return hours.getHours() >= 3;
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
