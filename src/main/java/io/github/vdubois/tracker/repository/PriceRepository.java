package io.github.vdubois.tracker.repository;

import io.github.vdubois.tracker.domain.Price;
import io.github.vdubois.tracker.domain.ProductToTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the Price entity.
 */
public interface PriceRepository extends JpaRepository<Price,Long> {

    @Query("select price from Price price where price.productToTrack.user.login = ?#{principal.username}")
    List<Price> findAllForCurrentUser();

    List<Price> findAllByProductToTrack(ProductToTrack productToTrack);
}
