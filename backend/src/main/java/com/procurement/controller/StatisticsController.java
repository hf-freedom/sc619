package com.procurement.controller;

import com.procurement.model.ProcurementStatistics;
import com.procurement.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<ProcurementStatistics> getStatistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
}
