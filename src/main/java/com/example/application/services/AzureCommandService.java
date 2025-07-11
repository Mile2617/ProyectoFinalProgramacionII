package com.example.application.services;

import org.springframework.stereotype.Service;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

@Service
public class AzureCommandService {
    private final String iotHubHost = "<YOUR_IOTHUB_NAME>.azure-devices.net";
    private final String deviceId = "EhabDevID";
    private final String sasToken = "<YOUR_SAS_TOKEN>"; // Generate with Azure Portal or CLI

    public void sendReleCommandToAzure(int value) {
        sendDirectMethod("SetRelay", "{\"value\":" + value + "}");
    }

    public void sendBuzzerCommandToAzure(int value) {
        sendDirectMethod("SetBuzzer", "{\"value\":" + value + "}");
    }

    private void sendDirectMethod(String methodName, String payload) {
        try {
            String url = "https://" + iotHubHost + "/twins/" + deviceId + "/methods?api-version=2020-09-30";
            String body = "{ \"methodName\": \"" + methodName + "\", \"responseTimeoutInSeconds\": 30, \"payload\": " + payload + " }";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", sasToken)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> System.out.println("Azure response: " + response.body()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}