// Java
package com.example.application.services;

import org.springframework.stereotype.Service;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

@Service
public class AzureCommandService {
    private final String iotHubHost = "EhabloTHub1.azure-devices.net";
    private final String deviceId = "EhabDevID";
    private final String sasToken = "SharedAccessSignature sr=EhabloTHub1.azure-devices.net%2Fdevices%2FEhabDevID&sig=p%2FsusKyjEVq7bWpoudI51E7Rhc3mYnU0NCen2yHP33w%3D&se=1752260407";

    public void sendDirectMethod(String methodName, Object payload) {
        try {
            String url = "https://" + iotHubHost + "/twins/" + deviceId + "/methods?api-version=2020-09-30";
            String body = "{ \"methodName\": \"" + methodName + "\", \"responseTimeoutInSeconds\": 30, \"payload\": " + (payload == null ? "{}" : payload.toString()) + " }";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", sasToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> System.out.println("Azure response: " + response.body()));
        } catch (Exception e) {
            System.err.println("[AZURE CMD ERROR] " + e.getMessage());
        }
    }

    public void sendReleCommand(boolean on) {
        sendDirectMethod(on ? "rele_on" : "rele_off", null);
    }

    public void sendBuzzerCommand(boolean on) {
        sendDirectMethod(on ? "buzzer_on" : "buzzer_off", null);
    }

    public void sendRele2Command(boolean on) {
        sendDirectMethod(on ? "rele2_on" : "rele2_off", null);
    }
}