package umg.edu.gt.desarrollo.proyectocovidstats.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import umg.edu.gt.desarrollo.proyectocovidstats.config.AppConfig;
import umg.edu.gt.desarrollo.proyectocovidstats.util.ApiClient;

@Service
public class ApiService {

    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    private final ApiClient apiClient;
    private final CovidDataService covidDataService;
    private final AppConfig appConfig;

    public ApiService(ApiClient apiClient, CovidDataService covidDataService, AppConfig appConfig) {
        this.apiClient = apiClient;
        this.covidDataService = covidDataService;
        this.appConfig = appConfig;
    }

    public void fetchCovidData() {
        logger.info("Starting COVID-19 data fetch");
        String countryIso = appConfig.getCountryIso();
        String reportDate = appConfig.getReportDate();

        logger.info("Configuration: countryIso='{}', reportDate='{}'", countryIso, reportDate);

        try {
            processRegions();
            processProvinces(countryIso);
            processReports(countryIso, reportDate);

            logger.info("Fetch process completed successfully");
        } catch (Exception e) {
            logger.error("Error during fetch process: ", e);
            throw new RuntimeException("Error processing COVID data", e);
        }
    }

    private void processRegions() throws Exception {
        String regions = apiClient.getRegions();
        logger.debug("Region data obtained");
        covidDataService.saveRegions(regions);
    }

    private void processProvinces(String countryIso) throws Exception {
        String provinces = apiClient.getProvinces(countryIso);
        logger.debug("Province data obtained for {}", countryIso);
        covidDataService.saveProvinces(provinces, countryIso);
    }

    private void processReports(String countryIso, String reportDate) throws Exception {
        String report = apiClient.getReport(countryIso, reportDate);
        logger.debug("Report obtained for {} on date {}", countryIso, reportDate);
        covidDataService.saveReports(report);
    }
}
