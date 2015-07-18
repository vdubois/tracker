package io.github.vdubois.tracker.service;

import io.github.vdubois.tracker.domain.Price;
import io.github.vdubois.tracker.domain.ProductToTrack;
import io.github.vdubois.tracker.repository.PriceRepository;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;

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
    private JobLauncher jobLauncher;

    @Inject
    private Job refreshPricesJob;
    
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
    public void refreshPrices() {
        try {
            jobLauncher.run(refreshPricesJob, new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters());
        } catch (JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException | JobRestartException jobExecutionAlreadyRunningException) {
            log.severe(ExceptionUtils.getRootCauseMessage(jobExecutionAlreadyRunningException));
        }
    }
}
