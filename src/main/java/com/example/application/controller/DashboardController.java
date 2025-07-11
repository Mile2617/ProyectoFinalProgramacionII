// Java
package com.example.application.controller;

import com.example.application.services.SensorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final SensorService sensorService;

    public DashboardController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping("/rele")
    public void setRele(@RequestParam int value) {
        sensorService.setRele(value);
    }

    @PostMapping("/buzzer")
    public void setBuzzer(@RequestParam int value) {
        sensorService.setBuzzer(value);
    }

    @PostMapping("/rele2")
    public void setRele2(@RequestParam int value) {
        sensorService.setRele2(value);
    }

    @GetMapping("/status")
    public String getStatus() {
        return sensorService.getStatus();
    }
}