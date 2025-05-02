package umg.edu.gt.desarrollo.proyectocovidstats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Report;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Buscar todos los reportes por el nombre de la provincia
    List<Report> findByProvince_Name(String name);

    // Buscar un reporte por provincia y fecha
    Optional<Report> findByProvinceAndReportDate(Province province, Date reportDate);

    // Si usas LocalDate en la base de datos, puedes usar este método
    Optional<Report> findByProvinceAndReportDate(Province province, java.time.LocalDate reportDate);
}
