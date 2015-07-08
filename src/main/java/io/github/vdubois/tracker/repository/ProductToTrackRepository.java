package io.github.vdubois.tracker.repository;

import io.github.vdubois.tracker.domain.ProductToTrack;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProductToTrack entity.
 */
public interface ProductToTrackRepository extends JpaRepository<ProductToTrack,Long> {

    @Query("select productToTrack from ProductToTrack productToTrack where productToTrack.user.login = ?#{principal.username}")
    List<ProductToTrack> findAllForCurrentUser();

}
