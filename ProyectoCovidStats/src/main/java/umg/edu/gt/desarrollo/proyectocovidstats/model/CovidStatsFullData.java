package umg.edu.gt.desarrollo.proyectocovidstats.model;

import javax.persistence.*;

@Entity
public class CovidStatsFullData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id")
    private Province province;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    // Otros campos que puedas necesitar, por ejemplo:
    private Integer totalCases;
    private Integer totalDeaths;
    private Integer totalRecovered;

    // Constructor sin argumentos
    public CovidStatsFullData() {
    }

    // Constructor con los campos necesarios
    public CovidStatsFullData(Region region, Province province, Report report,
                              Integer totalCases, Integer totalDeaths, Integer totalRecovered) {
        this.region = region;
        this.province = province;
        this.report = report;
        this.totalCases = totalCases;
        this.totalDeaths = totalDeaths;
        this.totalRecovered = totalRecovered;
    }

    // Getter y Setter para 'id'
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter y Setter para 'region'
    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    // Getter y Setter para 'province'
    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    // Getter y Setter para 'report'
    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    // Getter y Setter para 'totalCases'
    public Integer getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(Integer totalCases) {
        this.totalCases = totalCases;
    }

    // Getter y Setter para 'totalDeaths'
    public Integer getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(Integer totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    // Getter y Setter para 'totalRecovered'
    public Integer getTotalRecovered() {
        return totalRecovered;
    }

    public void setTotalRecovered(Integer totalRecovered) {
        this.totalRecovered = totalRecovered;
    }

    @Override
    public String toString() {
        return "CovidStatsFullData{" +
                "id=" + id +
                ", region=" + region.getName() + // Asumiendo que tienes un método getName() en Region
                ", province=" + province.getName() + // Asumiendo que tienes un método getName() en Province
                ", report=" + report.getReportName() + // Asumiendo que tienes un método getReportName() en Report
                ", totalCases=" + totalCases +
                ", totalDeaths=" + totalDeaths +
                ", totalRecovered=" + totalRecovered +
                '}';
    }
}
