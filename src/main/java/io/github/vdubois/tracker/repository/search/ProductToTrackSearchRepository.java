package io.github.vdubois.tracker.repository.search;

import io.github.vdubois.tracker.domain.ProductToTrack;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ProductToTrack entity.
 */
public interface ProductToTrackSearchRepository extends ElasticsearchRepository<ProductToTrack, Long> {
}
