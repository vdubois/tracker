package io.github.vdubois.tracker.repository.search;

import io.github.vdubois.tracker.domain.Store;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Store entity.
 */
public interface StoreSearchRepository extends ElasticsearchRepository<Store, Long> {
}
