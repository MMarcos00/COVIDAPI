package umg.edu.gt.desarrollo.proyectocovidstats.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "provinces")
public class Province {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "iso_code")
    private String isoCode;

    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    @OneToMany(mappedBy = "province")
    private List<CovidStatsFullData> covidStatsFullDataList;

    @OneToMany(mappedBy = "province")
    private List<Report> reportList;

    // Constructor vacío
    public Province() {
    }

    // Constructor con parámetros (opcional)
    public Province(String name, String isoCode, Region region) {
        this.name = name;
        this.isoCode = isoCode;
        this.region = region;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public List<CovidStatsFullData> getCovidStatsFullDataList() {
        return covidStatsFullDataList;
    }

    public void setCovidStatsFullDataList(List<CovidStatsFullData> covidStatsFullDataList) {
        this.covidStatsFullDataList = covidStatsFullDataList;
    }

    public List<Report> getReportList() {
        return reportList;
    }

    public void setReportList(List<Report> reportList) {
        this.reportList = reportList;
    }
}
