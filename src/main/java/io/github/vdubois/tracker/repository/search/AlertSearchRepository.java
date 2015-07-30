package io.github.vdubois.tracker.repository.search;

import io.github.vdubois.tracker.domain.Alert;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Alert entity.
 */
public interface AlertSearchRepository extends ElasticsearchRepository<Alert, Long> {
}
