package umg.edu.gt.desarrollo.proyectocovidstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Region;

import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {  // Cambié de String a Long
    Optional<Region> findByIsoCode(String isoCode);
}
