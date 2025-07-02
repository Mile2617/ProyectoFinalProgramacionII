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
    public Flux<Double> getStockPrice() {

        return Flux
                .<Double>generate(
                        sink -> {
                            sink.next(sensor.getValue());
                        }
                )
                .delayElements(Duration.ofMillis(1000));
        //.take(30);
    }
}
