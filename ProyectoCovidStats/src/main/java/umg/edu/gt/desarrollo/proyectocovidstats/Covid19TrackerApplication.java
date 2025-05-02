package umg.edu.gt.desarrollo.proyectocovidstats;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Region;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Report;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.RegionRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ProvinceRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ReportRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.service.CovidDataService;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;

@EnableRetry
@SpringBootApplication
@Log4j2
@RequiredArgsConstructor
public class Covid19TrackerApplication implements CommandLineRunner {

    private final CovidDataService covidDataService;
    private final RestTemplate restTemplate;
    private final RegionRepository regionRepository;
    private final ProvinceRepository provinceRepository;
    private final ReportRepository reportRepository;

    @Value("${covid.api.base-url:https://covid-19-statistics.p.rapidapi.com}")
    private String apiUrl;

    @Value("${covid.api.key}")
    private String apiKey;

    public static void main(String[] args) {
        SpringApplication.run(Covid19TrackerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            List<Region> regions = fetchRegionsFromApi();
            List<Province> provinces = fetchProvincesFromApi();
            List<Report> reports = fetchReportsFromApi();

            saveRegions(regions);
            saveProvinces(provinces);
            saveReports(reports);

        } catch (Exception e) {
            log.error("Error al cargar datos COVID", e);
        }
    }

    private List<Region> fetchRegionsFromApi() {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .path("/regions")
                .toUriString();

        ResponseEntity<List<Region>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Region>>() {}
        );

        return response.getBody();
    }

    private List<Province> fetchProvincesFromApi() {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .path("/provinces")
                .toUriString();

        ResponseEntity<List<Province>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Province>>() {}
        );

        return response.getBody();
    }

    private List<Report> fetchReportsFromApi() {
        String url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .path("/reports")
                .toUriString();

        ResponseEntity<List<Report>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Report>>() {}
        );

        return response.getBody();
    }

    private void saveRegions(List<Region> regions) {
        for (Region region : regions) {
            if (!regionRepository.existsById(region.getId())) {
                regionRepository.save(region);
                log.info("Guardada la región: {}", region.getName());
            }
        }
    }

    private void saveProvinces(List<Province> provinces) {
        for (Province province : provinces) {
            if (!provinceRepository.existsById(province.getId())) {
                provinceRepository.save(province);
                log.info("Guardada la provincia: {}", province.getName());
            }
        }
    }

    private void saveReports(List<Report> reports) {
        for (Report report : reports) {
            if (!reportRepository.existsById(report.getId())) {
                reportRepository.save(report);
                log.info("Guardado el reporte con fecha: {}", report.getReportDate());
            }
        }
    }
}
