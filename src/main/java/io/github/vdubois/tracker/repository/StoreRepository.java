package io.github.vdubois.tracker.repository;

import io.github.vdubois.tracker.domain.Store;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Store entity.
 */
public interface StoreRepository extends JpaRepository<Store,Long> {

    @Query("select store from Store store where store.user.login = ?#{principal.username}")
    List<Store> findAllForCurrentUser();

}
