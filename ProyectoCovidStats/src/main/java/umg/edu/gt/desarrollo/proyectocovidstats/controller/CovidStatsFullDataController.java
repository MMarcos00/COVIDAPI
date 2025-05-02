package umg.edu.gt.desarrollo.proyectocovidstats.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import umg.edu.gt.desarrollo.proyectocovidstats.model.CovidStatsFullData;
import umg.edu.gt.desarrollo.proyectocovidstats.service.CovidStatsFullDataService;

@RestController
@RequestMapping("/api/data")
public class CovidStatsFullDataController {

    @Autowired
    private CovidStatsFullDataService service;

    @PostMapping
    public CovidStatsFullData save(@RequestBody CovidStatsFullData data) {
        return service.saveData(data);
    }
}
