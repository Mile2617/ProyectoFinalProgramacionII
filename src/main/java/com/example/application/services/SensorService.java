// Java
package com.example.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SensorService {

    @Autowired
    private AzureCommandService azureCommandService;

    private int estadoRele = 0;
    private int estadoRele2 = 0;
    private int estadoBuzzer = 0;

    public void setRele(int value) {
        estadoRele = value;
        azureCommandService.sendReleCommand(value == 1);
    }

    public void setBuzzer(int value) {
        estadoBuzzer = value;
        azureCommandService.sendBuzzerCommand(value == 1);
    }

    public void setRele2(int value) {
        estadoRele2 = value;
        azureCommandService.sendRele2Command(value == 1);
    }

    public String getStatus() {
        return String.format("Rele 1: %s, Rele 2: %s, Buzzer: %s",
                estadoRele == 1 ? "ON" : "OFF",
                estadoRele2 == 1 ? "ON" : "OFF",
                estadoBuzzer == 1 ? "ON" : "OFF");
    }
}