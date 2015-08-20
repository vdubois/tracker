package io.github.vdubois.tracker.service;

import com.gargoylesoftware.htmlunit.WebClient;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by vdubois on 18/08/15.
 */
@Service
@EnableScheduling
@Log
public class PingService {

    @Value(value = "${ping.url}")
    private String selfURL;
    
    /**
     * Service that pings the backend
     */
    @Scheduled(cron = "0 0/3 * * * *")
    @Async
    public void ping() {
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
        try {
            webClient.getPage(selfURL);
            log.fine("Ping to " + selfURL + " succeeded");
        } catch (IOException ioException) {
            log.severe(ioException.getMessage());
        }
    }
}
