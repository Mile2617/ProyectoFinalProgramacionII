package com.example.application.threads;

import com.azure.messaging.eventhubs.*;
import com.azure.messaging.eventhubs.models.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.Disposable;

import java.util.concurrent.atomic.AtomicReference;

public class SensorRead extends Thread {

    private final String connectionString = "Endpoint=sb://ihsuproduaenorthres005dednamespace.servicebus.windows.net/;SharedAccessKeyName=iothubowner;SharedAccessKey=TvpwGAeq0unl6CN5utp7t7rMjyPdOPDN3AIoTNIWXvY=;EntityPath=iothub-ehub-ehablothub-55382497-01de6bb357";
    private final String eventHubName = "iothub-ehub-ehablothub-55382497-01de6bb357";
    private final String consumerGroup = "java-monitor"; // 👈 usa uno nuevo

    private final AtomicReference<Double> temp1 = new AtomicReference<>(0.0);
    private final AtomicReference<Integer> humo1 = new AtomicReference<>(0);

    @Override
    public void run() {
        EventHubConsumerAsyncClient asyncClient = new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .consumerGroup(consumerGroup)
                .buildAsyncConsumerClient();

        System.out.println("🔌 Streaming client connected. Waiting for real-time events...");

        asyncClient.getPartitionIds().subscribe(partitionId -> {
            Disposable subscription = asyncClient.receiveFromPartition(partitionId, EventPosition.latest())
                    .subscribe(event -> {
                        try {
                            String json = event.getData().getBodyAsString();
                            System.out.println("📥 Received telemetry: " + json);

                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode body = mapper.readTree(json);

                            if (body.has("Temp1") && !body.get("Temp1").isNull()) {
                                double t = body.get("Temp1").asDouble();
                                temp1.set(t);
                                System.out.println("🌡 Temp1: " + t);
                            }

                            if (body.has("Humo1") && !body.get("Humo1").isNull()) {
                                int h = body.get("Humo1").asInt();
                                humo1.set(h);
                                System.out.println("💨 Humo1: " + h);
                            }

                        } catch (Exception e) {
                            System.err.println("❌ JSON parse error: " + e.getMessage());
                        }
                    }, error -> {
                        System.err.println("❌ Streaming error: " + error.getMessage());
                    });

            System.out.println("📡 Listening on partition " + partitionId);
        });
    }

    public double getTemp1() {
        return temp1.get();
    }

    public int getHumo1() {
        return humo1.get();
    }
}
