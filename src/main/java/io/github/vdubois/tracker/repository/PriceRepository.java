package io.github.vdubois.tracker.repository;

import io.github.vdubois.tracker.domain.Price;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Price entity.
 */
public interface PriceRepository extends JpaRepository<Price,Long> {

}
