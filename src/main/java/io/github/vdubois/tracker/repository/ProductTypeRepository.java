package io.github.vdubois.tracker.repository;

import io.github.vdubois.tracker.domain.ProductType;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProductType entity.
 */
public interface ProductTypeRepository extends JpaRepository<ProductType,Long> {

    @Query("select productType from ProductType productType where productType.user.login = ?#{principal.username}")
    List<ProductType> findAllForCurrentUser();

}
