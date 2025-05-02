package umg.edu.gt.desarrollo.proyectocovidstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.model.CovidStatsFullData;

public interface CovidStatsFullDataRepository extends JpaRepository<CovidStatsFullData, Long> {
}
