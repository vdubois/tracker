package io.github.vdubois.tracker.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.vdubois.tracker.domain.Alert;
import io.github.vdubois.tracker.repository.AlertRepository;
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
 * REST controller for managing Alert.
 */
@RestController
@RequestMapping("/api")
public class AlertResource {

    private final Logger log = LoggerFactory.getLogger(AlertResource.class);

    @Inject
    private AlertRepository alertRepository;

    /**
     * POST  /alerts -> Create a new alert.
     */
    @RequestMapping(value = "/alerts",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Alert alert) throws URISyntaxException {
        log.debug("REST request to save Alert : {}", alert);
        if (alert.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new alert cannot already have an ID").build();
        }
        alertRepository.save(alert);
        return ResponseEntity.created(new URI("/api/alerts/" + alert.getId())).build();
    }

    /**
     * PUT  /alerts -> Updates an existing alert.
     */
    @RequestMapping(value = "/alerts",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Alert alert) throws URISyntaxException {
        log.debug("REST request to update Alert : {}", alert);
        if (alert.getId() == null) {
            return create(alert);
        }
        alertRepository.save(alert);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /alerts -> get all the alerts.
     */
    @RequestMapping(value = "/alerts",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Alert>> getAll(@RequestParam(value = "page", required = false) Integer offset,
                                              @RequestParam(value = "per_page", required = false) Integer limit)
            throws URISyntaxException {
        List<Alert> alerts = alertRepository.findAllForCurrentUser();
        return new ResponseEntity<>(alerts, HttpStatus.OK);
    }

    /**
     * GET  /alerts/:id -> get the "id" alert.
     */
    @RequestMapping(value = "/alerts/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Alert> get(@PathVariable Long id) {
        log.debug("REST request to get Alert : {}", id);
        return Optional.ofNullable(alertRepository.findOne(id))
            .map(alert -> new ResponseEntity<>(
                alert,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /alerts/:id -> delete the "id" alert.
     */
    @RequestMapping(value = "/alerts/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Alert : {}", id);
        alertRepository.delete(id);
    }
}
