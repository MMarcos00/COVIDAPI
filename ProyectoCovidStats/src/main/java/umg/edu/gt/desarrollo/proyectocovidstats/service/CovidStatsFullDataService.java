package umg.edu.gt.desarrollo.proyectocovidstats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import umg.edu.gt.desarrollo.proyectocovidstats.model.CovidStatsFullData;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Region;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Report;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.CovidStatsFullDataRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ProvinceRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.RegionRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ReportRepository;

@Service
public class CovidStatsFullDataService {

    @Autowired
    private CovidStatsFullDataRepository covidStatsFullDataRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private ReportRepository reportRepository;

    public CovidStatsFullData saveData(CovidStatsFullData data) {
        // Obtener la región, provincia y reporte de la base de datos
        Region region = regionRepository.findById(data.getRegion().getId())
                .orElseThrow(() -> new IllegalArgumentException("La región no existe en la base de datos."));

        Province province = provinceRepository.findById(data.getProvince().getId())
                .orElseThrow(() -> new IllegalArgumentException("La provincia no existe en la base de datos."));

        Report report = reportRepository.findById(data.getReport().getId())
                .orElseThrow(() -> new IllegalArgumentException("El reporte no existe en la base de datos."));

        // Establecer los objetos obtenidos en el objeto data
        data.setRegion(region);
        data.setProvince(province);
        data.setReport(report);

        // Guardar y retornar el dato
        return covidStatsFullDataRepository.save(data);
    }
}
