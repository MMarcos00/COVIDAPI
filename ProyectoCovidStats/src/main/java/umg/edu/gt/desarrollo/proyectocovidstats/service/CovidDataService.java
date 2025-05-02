package umg.edu.gt.desarrollo.proyectocovidstats.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umg.edu.gt.desarrollo.proyectocovidstats.model.CovidStatsFullData;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Province;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Region;
import umg.edu.gt.desarrollo.proyectocovidstats.model.Report;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ProvinceRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.RegionRepository;
import umg.edu.gt.desarrollo.proyectocovidstats.repository.ReportRepository;

import java.time.LocalDate;
import java.util.*;

@Service
public class CovidDataService {

    private static final Logger logger = LoggerFactory.getLogger(CovidDataService.class);
    private static final String DEFAULT_PROVINCE_NAME = "N/A";
    private static final String DATA_NODE = "data";

    private final RegionRepository regionRepository;
    private final ProvinceRepository provinceRepository;
    private final ReportRepository reportRepository;
    private final ObjectMapper objectMapper;

    private final Map<String, Region> isoRegionMap = new HashMap<>();
    private final Map<String, Province> provinceMap = new HashMap<>();

    public CovidDataService(RegionRepository regionRepository,
                            ProvinceRepository provinceRepository,
                            ReportRepository reportRepository) {
        this.regionRepository = regionRepository;
        this.provinceRepository = provinceRepository;
        this.reportRepository = reportRepository;
        this.objectMapper = new ObjectMapper();
    }

    public static void saveCovidStats(List<CovidStatsFullData> covidStats) {
    }

    @Transactional
    public void saveRegions(String json) throws Exception {
        if (json == null || json.isEmpty()) {
            throw new IllegalArgumentException("JSON cannot be empty");
        }

        JsonNode root = objectMapper.readTree(json);
        JsonNode dataNode = root.get(DATA_NODE);

        if (dataNode == null || !dataNode.isArray()) {
            throw new IllegalArgumentException("Invalid JSON format");
        }

        List<Region> regionsToSave = new ArrayList<>();
        for (JsonNode node : dataNode) {
            String isoCode = node.get("iso").asText();
            String regionName = node.get("name").asText();

            Optional<Region> optionalRegion = regionRepository.findByIsoCode(isoCode);
            Region region = optionalRegion.orElseGet(() -> {
                Region newRegion = new Region();
                newRegion.setIsoCode(isoCode);
                newRegion.setName(regionName);
                return newRegion;
            });

            isoRegionMap.put(isoCode, region);
            regionsToSave.add(region);
        }

        if (!regionsToSave.isEmpty()) {
            regionRepository.saveAll(regionsToSave);
            logger.info("Saved " + regionsToSave.size() + " regions");
        }
    }

    @Transactional
    public void saveProvinces(String json, String isoCode) throws Exception {
        if (json == null || json.isEmpty() || isoCode == null || isoCode.isEmpty()) {
            throw new IllegalArgumentException("JSON or ISO code cannot be empty");
        }

        Region region = regionRepository.findByIsoCode(isoCode)
                .orElseThrow(() -> new Exception("Region not found for ISO code: " + isoCode));

        JsonNode root = objectMapper.readTree(json);
        JsonNode dataNode = root.get(DATA_NODE);

        if (dataNode == null || !dataNode.isArray()) {
            throw new IllegalArgumentException("Invalid JSON format");
        }

        List<Province> provincesToSave = new ArrayList<>();

        for (JsonNode node : dataNode) {
            String provinceName = node.get("province").asText();
            if (provinceName.isEmpty()) provinceName = DEFAULT_PROVINCE_NAME;

            String provinceId = node.has("iso") ? node.get("iso").asText() :
                    region.getIsoCode() + "_" + provinceName.replaceAll("\\s+", "_");

            Optional<Province> optionalProvince = provinceRepository.findByNameAndRegion(provinceName, region);
            String finalProvinceName = provinceName;
            Province province = optionalProvince.orElseGet(() -> {
                Province newProvince = new Province();
                newProvince.setIsoCode(provinceId);
                newProvince.setName(finalProvinceName);
                newProvince.setRegion(region);
                return newProvince;
            });

            provincesToSave.add(province);
        }

        if (!provincesToSave.isEmpty()) {
            provinceRepository.saveAll(provincesToSave);
            logger.info("Saved " + provincesToSave.size() + " provinces");
        }
    }

    @Transactional
    public void saveReports(String json) throws Exception {
        if (json == null || json.isEmpty()) {
            throw new IllegalArgumentException("JSON cannot be empty");
        }

        JsonNode root = objectMapper.readTree(json);
        JsonNode dataNode = root.get(DATA_NODE);

        if (dataNode == null || !dataNode.isArray()) {
            throw new IllegalArgumentException("Invalid JSON format");
        }

        List<Report> reportsToSave = new ArrayList<>();

        for (JsonNode node : dataNode) {
            try {
                processReportNode(node, reportsToSave);
            } catch (Exception e) {
                logger.error("Error processing report: " + e.getMessage());
            }
        }

        if (!reportsToSave.isEmpty()) {
            reportRepository.saveAll(reportsToSave);
            logger.info("Saved " + reportsToSave.size() + " reports");
        } else {
            logger.info("No reports found to save");
        }
    }

    private void processReportNode(JsonNode node, List<Report> reportsToSave) throws Exception {
        JsonNode regionNode = node.get("region");
        String provinceName = regionNode.get("province").asText();
        String regionIsoCode = regionNode.has("iso") ? regionNode.get("iso").asText() : "";

        Province province = findProvince(provinceName, regionIsoCode);
        if (province == null) {
            logger.warn("Province not found: " + provinceName + " in region: " + regionIsoCode);
            return;
        }

        LocalDate reportDate = LocalDate.parse(node.get("date").asText());
        Optional<Report> optionalReport = reportRepository.findByProvinceAndReportDate(province, reportDate);
        Report report = optionalReport.orElse(new Report());
        report.setDate(reportDate);
        report.setProvince(province);

        updateReportData(report, node);
        reportsToSave.add(report);
    }

    private Province findProvince(String provinceName, String regionIsoCode) {
        return provinceRepository.findByNameAndRegion(provinceName, isoRegionMap.get(regionIsoCode))
                .orElse(null);
    }

    private void updateReportData(Report report, JsonNode node) {
        report.setTotalCases(node.get("confirmed").asInt());
        report.setNewCases(node.has("confirmed_diff") ? node.get("confirmed_diff").asInt() : 0);
        report.setTotalDeaths(node.get("deaths").asInt());
        report.setNewDeaths(node.has("deaths_diff") ? node.get("deaths_diff").asInt() : 0);
    }
}
