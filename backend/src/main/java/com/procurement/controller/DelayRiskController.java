package com.procurement.controller;

import com.procurement.model.DelayRisk;
import com.procurement.service.DelayRiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delay-risks")
public class DelayRiskController {
    @Autowired
    private DelayRiskService delayRiskService;

    @GetMapping
    public ResponseEntity<List<DelayRisk>> getAllDelayRisks() {
        return ResponseEntity.ok(delayRiskService.getAllDelayRisks());
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<DelayRisk> resolveRisk(@PathVariable String id) {
        DelayRisk risk = delayRiskService.resolveRisk(id);
        return risk != null ? ResponseEntity.ok(risk) : ResponseEntity.notFound().build();
    }
}
