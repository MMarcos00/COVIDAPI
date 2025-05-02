package umg.edu.gt.desarrollo.proyectocovidstats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import umg.edu.gt.desarrollo.proyectocovidstats.model.CovidStatsFullData;
import umg.edu.gt.desarrollo.proyectocovidstats.util.ApiClient;

import java.util.List;

@Service
public class ApiCovidDataLoader {

    private final ApiClient apiClient;
    private final CovidDataService covidDataService;

    @Autowired
    public ApiCovidDataLoader(ApiClient apiClient, CovidDataService covidDataService) {
        this.apiClient = apiClient;
        this.covidDataService = covidDataService;
    }

    public void loadAndSaveData() {
        System.out.println("Cargando datos desde la API externa...");

        try {
            // 1. Obtener los datos de la API
            List<CovidStatsFullData> covidStats = apiClient.fetchCovidStatsData();

            // 2. Guardar los datos en la base de datos
            if (!covidStats.isEmpty()) {
                covidDataService.saveCovidStats(covidStats);
                System.out.println("Datos de COVID-19 cargados y guardados correctamente.");
            } else {
                System.out.println("No se encontraron datos para cargar.");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar los datos desde la API externa: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
