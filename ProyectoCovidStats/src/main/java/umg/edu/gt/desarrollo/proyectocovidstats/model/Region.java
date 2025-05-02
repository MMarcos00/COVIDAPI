package umg.edu.gt.desarrollo.proyectocovidstats.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "regions")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "iso_code")
    private String isoCode;

    @OneToMany(mappedBy = "region")
    private List<CovidStatsFullData> covidStatsFullDataList;

    @OneToMany(mappedBy = "region")
    private List<Province> provinces;

    // Constructor vacío
    public Region() {
    }

    // Constructor con parámetros (opcional)
    public Region(String name, String isoCode) {
        this.name = name;
        this.isoCode = isoCode;
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

    public List<CovidStatsFullData> getCovidStatsFullDataList() {
        return covidStatsFullDataList;
    }

    public void setCovidStatsFullDataList(List<CovidStatsFullData> covidStatsFullDataList) {
        this.covidStatsFullDataList = covidStatsFullDataList;
    }

    public List<Province> getProvinces() {
        return provinces;
    }

    public void setProvinces(List<Province> provinces) {
        this.provinces = provinces;
    }
}
