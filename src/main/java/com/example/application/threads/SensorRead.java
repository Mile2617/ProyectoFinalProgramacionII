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
    private final String consumerGroup = "java-monitor";

    private Double temp1 = 0.0;
    private Double temp2 = 0.0;
    private final AtomicReference<Integer> humo1 = new AtomicReference<>(0);
    private final AtomicReference<Integer> humo2 = new AtomicReference<>(0);
    private final AtomicReference<Boolean> fuego = new AtomicReference<>(false);

    @Override
    public void run() {
        try {
            EventHubConsumerAsyncClient asyncClient = new EventHubClientBuilder()
                    .connectionString(connectionString, eventHubName)
                    .consumerGroup(consumerGroup)
                    .buildAsyncConsumerClient();

            System.out.println("🔌 Streaming client connected. Waiting for real-time events...");
            Thread.sleep(1000);
            asyncClient.getPartitionIds().subscribe(partitionId -> {
                Disposable subscription = asyncClient.receiveFromPartition(partitionId, EventPosition.latest())
                        .subscribe(event -> {
                            try {
                                String json = event.getData().getBodyAsString();
                                // Replace all 'nan' (case-insensitive) with 'null'
                                json = json.replaceAll("(?i)nan", "null");
                                System.out.println("📥 Received telemetry: " + json);

                                ObjectMapper mapper = new ObjectMapper();
                                JsonNode body = mapper.readTree(json);

                                if (body.has("Temp1") && !body.get("Temp1").isNull()) {
                                    double t1 = body.get("Temp1").asDouble();
                                    temp1 = t1;
                                    System.out.println("🌡 Temp1: " + t1);
                                }
                                if (body.has("Temp2") && !body.get("Temp2").isNull()) {
                                    double t2 = body.get("Temp2").asDouble();
                                    temp2 = t2;
                                    System.out.println("🌡 Temp2: " + t2);
                                }
                                if (body.has("Humo1") && !body.get("Humo1").isNull()) {
                                    int h1 = body.get("Humo1").asInt();
                                    humo1.set(h1);
                                    System.out.println("💨 Humo1: " + h1);
                                }
                                if (body.has("Humo2") && !body.get("Humo2").isNull()) {
                                    int h2 = body.get("Humo2").asInt();
                                    humo2.set(h2);
                                    System.out.println("💨 Humo2: " + h2);
                                }
                                if (body.has("Fuego") && !body.get("Fuego").isNull()) {
                                    boolean f = body.get("Fuego").asBoolean();
                                    fuego.set(f);
                                    System.out.println("🔥 Fuego: " + f);
                                }
                            } catch (Exception e) {
                                System.err.println("❌ JSON parse error: " + e.getMessage());
                            }
                        }, error -> {
                            System.err.println("❌ Streaming error: " + error.getMessage());
                        });

                System.out.println("📡 Listening on partition " + partitionId);
            });
        } catch (Exception e) {
            System.err.println("❌ Error starting streaming: " + e.getMessage());
        }
    }

    public double getTemp1() { return temp1; }
    public double getTemp2() { return temp2; }
    public int getHumo1() { return humo1.get(); }
    public int getHumo2() { return humo2.get(); }
    public boolean isFuego() { return fuego.get(); }
}