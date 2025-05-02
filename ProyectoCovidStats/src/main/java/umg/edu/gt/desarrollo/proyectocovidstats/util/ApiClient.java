package umg.edu.gt.desarrollo.proyectocovidstats.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;  // Importación correcta de TypeReference
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import umg.edu.gt.desarrollo.proyectocovidstats.model.CovidStatsFullData;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class ApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;

    // Endpoints
    private static final String REGIONS_ENDPOINT = "/regions";
    private static final String PROVINCES_ENDPOINT = "/provinces";
    private static final String REPORTS_ENDPOINT = "/reports";

    // Parámetros de la API
    private static final String ISO_PARAM = "iso";
    private static final String DATE_PARAM = "date";
    private static final String API_KEY_HEADER = "X-RapidAPI-Key";
    private static final String API_HOST_HEADER = "X-RapidAPI-Host";
    private static final String API_HOST = "covid-19-statistics.p.rapidapi.com";

    public ApiClient(
            RestTemplate restTemplate,
            @Value("${covid.api.key:2505eda46amshc60713983b5e807p1da25ajsn36febcbf4a71}") String apiKey,
            @Value("${covid.api.base-url:https://covid-19-statistics.p.rapidapi.com}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    // Método para obtener los datos de Covid
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public List<CovidStatsFullData> fetchCovidStatsData() throws Exception {
        URI uri = buildUri("/reports/total/country/{country}/status/{status}")
                .buildAndExpand("all", "confirmed") // Cambiar según el país o estado
                .toUri();

        String response = executeRequest(uri);

        // Parsear la respuesta JSON a una lista de objetos de tipo CovidStatsFullData
        return parseCovidStatsResponse(response);
    }

    // Método para obtener las regiones
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String getRegions() throws Exception {
        return null;
    }

    // Método para obtener las provincias por código ISO
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String getProvinces(String iso) throws Exception {
        if (iso == null || iso.trim().isEmpty()) {
            throw new IllegalArgumentException("ISO code cannot be empty");
        }

        logger.info("Getting provinces for ISO: {}", iso);
        URI uri = buildUri(PROVINCES_ENDPOINT)
                .queryParam(ISO_PARAM, iso)
                .build()
                .toUri();

        return executeRequest(uri);
    }

    // Método para obtener los reportes por código ISO y fecha
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String getReport(String iso, String date) throws Exception {
        validateReportParameters(iso, date);

        logger.info("Getting report for ISO: {} and date: {}", iso, date);
        URI uri = buildUri(REPORTS_ENDPOINT)
                .queryParam(ISO_PARAM, iso)
                .queryParam(DATE_PARAM, date)
                .build()
                .toUri();

        return executeRequest(uri);
    }

    // Construye la URI de la API con el endpoint proporcionado
    private UriComponentsBuilder buildUri(String endpoint) {
        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path(endpoint);
    }

    // Realiza la solicitud HTTP y maneja la respuesta
    private String executeRequest(URI uri) throws Exception {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(API_KEY_HEADER, apiKey);
            headers.set(API_HOST_HEADER, API_HOST);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return Optional.ofNullable(response.getBody())
                        .orElseThrow(() -> new Exception("Empty response from server"));
            } else {
                throw new Exception("Error in server response: " + response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Error making request to {}: {}", uri, e.getMessage());
            throw new Exception("Error communicating with API", e);
        }
    }

    // Valida los parámetros de la fecha y el código ISO
    private void validateReportParameters(String iso, String date) {
        if (iso == null || iso.trim().isEmpty()) {
            throw new IllegalArgumentException("ISO code cannot be empty");
        }
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be empty");
        }
        // Validar formato de fecha: YYYY-MM-DD
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Invalid date format. Use YYYY-MM-DD");
        }
    }

    private List<CovidStatsFullData> parseCovidStatsResponse(String response) {
        try {
            // Usar el ObjectMapper de Jackson para convertir el JSON a objetos Java
            ObjectMapper objectMapper = new ObjectMapper();

            // Deserializar el JSON en una lista de CovidStatsFullData
            return objectMapper.readValue(response, new TypeReference<List<CovidStatsFullData>>() {});
        } catch (JsonProcessingException e) {
            // Manejo de errores si la deserialización falla
            logger.error("Error al parsear la respuesta JSON: ", e);
            return Collections.emptyList(); // Retorna una lista vacía en caso de error
        }
    }

}
