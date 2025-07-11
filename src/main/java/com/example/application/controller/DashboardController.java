// src/main/java/com/example/application/controller/DashboardController.java
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
        sensorService.sendCommand(value, null, null);
    }

    @PostMapping("/buzzer")
    public void setBuzzer(@RequestParam int value) {
        sensorService.sendCommand(null, value, null);
    }

    @PostMapping("/rele2")
    public void setRele2(@RequestParam int value) {
        sensorService.sendCommand(null, null, value);
    }

    @GetMapping("/status")
    public String getStatus() {
        return String.format("Rele 1: %s, Rele 2: %s, Buzzer: %s",
                sensorService.getEstadoRele() == 1 ? "ON" : "OFF",
                sensorService.getEstadoRele2() == 1 ? "ON" : "OFF",
                sensorService.getEstadoBuzzer() == 1 ? "ON" : "OFF");
    }
}