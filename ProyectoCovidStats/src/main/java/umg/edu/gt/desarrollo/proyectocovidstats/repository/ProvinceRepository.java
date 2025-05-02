package umg.edu.gt.desarrollo.proyectocovidstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Region;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {  // Cambié de String a Long
    Optional<Province> findByNameAndRegion(String name, Region region);
    List<Province> findByNameContainingIgnoreCase(String name);
    List<Province> findByRegion(Region region);
}
