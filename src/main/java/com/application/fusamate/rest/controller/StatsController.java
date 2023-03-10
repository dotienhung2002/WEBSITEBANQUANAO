package com.application.fusamate.rest.controller;

import com.application.fusamate.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stats/")
@RequiredArgsConstructor
@Validated
@CrossOrigin("*")
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/status-count")
    public ResponseEntity<?> getStatsCountOfEmployee() {
        Map<Object, Object> map = new HashMap<>();
        map.put("data", statsService.getStatusCountStats());
        return ResponseEntity.ok().body(map);
    }

    @GetMapping("/overall-report")
    public ResponseEntity<?> getAnnualReport() {
        Map<Object, Object> map = new HashMap<>();
        map.put("data", statsService.getAnnualReport());
        return ResponseEntity.ok().body(map);
    }

    @GetMapping("/customer-growth-report")
    public ResponseEntity<?> getCustomerGrowthReport() {
        Map<Object, Object> map = new HashMap<>();
        map.put("data", statsService.getCustomerGrowthReport());
        return ResponseEntity.ok().body(map);
    }

    @GetMapping("/top-sales-customer")
    public ResponseEntity<?> getTopSalesCustomers(@RequestParam(required = false) Integer year,
                                                  @RequestParam(required = false) Integer month) {
        Map<Object, Object> map = new HashMap<>();
        map.put("data", statsService.getTopSalesCustomers(year, month).stream().limit(10));
        return ResponseEntity.ok().body(map);
    }

    @GetMapping("/top-sales-product")
    public ResponseEntity<?> getTopSalesProducts(@RequestParam(required = false) Integer year,
                                                  @RequestParam(required = false) Integer month) {
        Map<Object, Object> map = new HashMap<>();
        map.put("data", statsService.getTopSalesProducts(year, month).stream().limit(10));
        return ResponseEntity.ok().body(map);
    }
}
