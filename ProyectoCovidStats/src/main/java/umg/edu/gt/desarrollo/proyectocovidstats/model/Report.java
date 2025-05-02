package umg.edu.gt.desarrollo.proyectocovidstats.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_date")
    private LocalDate reportDate;

    private int totalCases;
    private int newCases;
    private int totalDeaths;
    private int newDeaths;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;

    public Report() {
    }

    public Report(LocalDate reportDate, int totalCases, int newCases, int totalDeaths, int newDeaths, Province province) {
        this.reportDate = reportDate;
        this.totalCases = totalCases;
        this.newCases = newCases;
        this.totalDeaths = totalDeaths;
        this.newDeaths = newDeaths;
        this.province = province;
    }

    public void setDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public void setTotalCases(int totalCases) {
        this.totalCases = totalCases;
    }

    public void setNewCases(int newCases) {
        this.newCases = newCases;
    }

    public void setTotalDeaths(int totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public void setNewDeaths(int newDeaths) {
        this.newDeaths = newDeaths;
    }

    public String getReportName() {
        return "Report for " + province.getName() + " on " + reportDate.toString();
    }
}
