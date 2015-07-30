package io.github.vdubois.tracker.repository;

import io.github.vdubois.tracker.domain.Alert;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Alert entity.
 */
public interface AlertRepository extends JpaRepository<Alert,Long> {

}
