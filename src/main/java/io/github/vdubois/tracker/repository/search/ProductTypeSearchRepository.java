package io.github.vdubois.tracker.repository.search;

import io.github.vdubois.tracker.domain.ProductType;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the ProductType entity.
 */
public interface ProductTypeSearchRepository extends ElasticsearchRepository<ProductType, Long> {
}
