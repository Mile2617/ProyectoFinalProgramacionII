// src/main/java/com/example/application/services/DataService.java
package com.example.application.services;

import com.example.application.threads.SensorRead;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
public class DataService {
    SensorRead sensor;
    public DataService() {
        sensor = new SensorRead();
        sensor.start();
    }
    public Flux<Double> getTemp1Stream() {
        return Flux.<Double>generate(sink -> sink.next(sensor.getTemp1()))
                .delayElements(Duration.ofMillis(1000));
    }
    public Flux<Integer> getHumo1Stream() {
        return Flux.<Integer>generate(sink -> sink.next(sensor.getHumo1()))
                .delayElements(Duration.ofMillis(1000));
    }
}