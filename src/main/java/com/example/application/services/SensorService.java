package com.example.application.services;

import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SensorService {
    private double temp1 = 0.0;
    private double temp2 = 0.0;
    private int humo1 = 0;
    private int humo2 = 0;
    private int flama1 = 0;
    private int flama2 = 0;
    private int estadoRele = 0;
    private int estadoRele2 = 0;
    private int estadoBuzzer = 0;
    private AtomicLong ultimoMensajeFlama = new AtomicLong(0);
    private AtomicLong ultimoMensajeHumo = new AtomicLong(0);
    private static final String CSV_FILE = "datos_iot.csv";
    private static final int COOLDOWN_FLAMA = 30;
    private static final int COOLDOWN_HUMO = 30;

    // Call this method for each telemetry event from Azure Event Hub
    public void handleTelemetry(JsonNode body) {
        temp1 = body.path("Temp1").asDouble(0.0);
        temp2 = body.path("Temp2").asDouble(0.0);
        humo1 = body.path("Humo1").asInt(0);
        humo2 = body.path("Humo2").asInt(0);
        flama1 = body.path("Flama1").asInt(0);
        flama2 = body.path("Flama2").asInt(0);

        logToCsv();

        long now = System.currentTimeMillis() / 1000;
        boolean riesgo = false;

        // Fire detection logic
        if (flama1 > 0 || flama2 > 0) {
            riesgo = true;
            if (now - ultimoMensajeFlama.get() > COOLDOWN_FLAMA) {
                sendCommand(1, 1, null); // Activate relay 1 and buzzer
                ultimoMensajeFlama.set(now);
            }
        }

        // Smoke detection logic
        if (humo1 > 1000 || humo2 > 1000) {
            riesgo = true;
            if (now - ultimoMensajeHumo.get() > COOLDOWN_HUMO) {
                sendCommand(1, 1, null); // Activate relay 1 and buzzer
                ultimoMensajeHumo.set(now);
            }
        }

        // If no risk, turn off relay and buzzer
        if (!riesgo) {
            sendCommand(0, 0, null);
        }
    }

    private void logToCsv() {
        try {
            boolean exists = new File(CSV_FILE).exists();
            FileWriter fw = new FileWriter(CSV_FILE, true);
            if (!exists) {
                fw.write("timestamp,temp1,temp2,humo1,humo2,flama1,flama2\n");
            }
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            fw.write(String.format("%s,%.2f,%.2f,%d,%d,%d,%d\n",
                    timestamp, temp1, temp2, humo1, humo2, flama1, flama2));
            fw.close();
        } catch (IOException e) {
            System.err.println("[ERROR CSV] " + e.getMessage());
        }
    }

    // Simulate relay/buzzer control (implement actual device logic as needed)
    public void sendCommand(Integer rele, Integer buzzer, Integer rele2) {
        if (rele != null) estadoRele = rele;
        if (buzzer != null) estadoBuzzer = buzzer;
        if (rele2 != null) estadoRele2 = rele2;
        // TODO: Implement actual device control (serial, HTTP, etc.) if needed
    }

    // Getters for dashboard/controllers
    public double getTemp1() { return temp1; }
    public double getTemp2() { return temp2; }
    public int getHumo1() { return humo1; }
    public int getHumo2() { return humo2; }
    public int getFlama1() { return flama1; }
    public int getFlama2() { return flama2; }
    public int getEstadoRele() { return estadoRele; }
    public int getEstadoRele2() { return estadoRele2; }
    public int getEstadoBuzzer() { return estadoBuzzer; }
    public boolean isRiesgo() { return (flama1 > 0 || flama2 > 0 || humo1 > 1000 || humo2 > 1000); }
}