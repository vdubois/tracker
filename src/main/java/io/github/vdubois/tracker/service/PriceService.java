package io.github.vdubois.tracker.service;

import io.github.vdubois.tracker.domain.Price;
import io.github.vdubois.tracker.domain.ProductToTrack;
import io.github.vdubois.tracker.repository.PriceRepository;
import io.github.vdubois.tracker.repository.ProductToTrackRepository;
import lombok.extern.java.Log;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.inject.Inject;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
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
        String productPageHTML = retrieveHTMLFromURL(productToTrack.getTrackingUrl());
        org.w3c.dom.Document document = extractDocumentFromHTML(productPageHTML);
        String price = findPricePyXPathInDocument(productToTrack.getTrackingDomSelector(), document);
        return new BigDecimal(price.trim().replaceAll("&euro;", ".").replaceAll("€", "."));
    }

    /**
     * @param xpathSelector xpath to find price node
     * @param document document where to find price node
     * @return price if found in document
     * @throws IOException I/O exception
     */
    private String findPricePyXPathInDocument(String xpathSelector, Document document) throws IOException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        String price;
        try {
            NodeList nodes = (NodeList) xpath.evaluate(xpathSelector, document, XPathConstants.NODESET);
            if (nodes.getLength() > 0) {
                price = nodes.item(0).getTextContent();
            } else {
                throw new IOException("No node corresponding to xpath");
            }
        } catch (XPathExpressionException xpathExpressionException) {
            throw new IOException(xpathExpressionException.getMessage(), xpathExpressionException);
        }
        return price;
    }

    /**
     * @param productPageHTML HTML from product page
     * @return Document from HTML
     */
    private Document extractDocumentFromHTML(String productPageHTML) {
        TagNode tagNode = new HtmlCleaner().clean(productPageHTML);
        Document document = null;
        try {
            document = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
        } catch (ParserConfigurationException parserConfigurationException) {
            log.severe(parserConfigurationException.getMessage());
        }
        return document;
    }

    /**
     * @param productToTrackURL product URL
     * @return HTML from product page
     * @throws IOException I/O exception
     */
    private String retrieveHTMLFromURL(String productToTrackURL) throws IOException {
        URL productURL = new URL(productToTrackURL);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(productURL.openStream()));
        String productPageHTML = "";
        String productPageHTMLLine;
        while ((productPageHTMLLine = bufferedReader.readLine()) != null) {
            productPageHTML += productPageHTMLLine;
        }
        bufferedReader.close();
        return productPageHTML;
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
        List<Price> prices = productToTrackRepository.findOne(productToTrack.getId())
                .getPricess()
                .stream()
                .sorted(Comparator.comparing(Price::getCreatedAt).reversed())
                .collect(Collectors.toList());
        DateTime now = DateTime.now();
        Hours hours = Hours.hoursBetween(prices.get(0).getCreatedAt(), now);
        log.warning("Hours since last refresh : " + hours.getHours());
        return hours.getHours() >= 3;
    }
}
