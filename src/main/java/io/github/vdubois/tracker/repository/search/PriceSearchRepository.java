package io.github.vdubois.tracker.repository.search;

import io.github.vdubois.tracker.domain.Price;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Price entity.
 */
public interface PriceSearchRepository extends ElasticsearchRepository<Price, Long> {
}
