package com.example.application.services;

import com.example.application.threads.SensorRead;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
public class DataService {
    private final SensorRead sensor;

    public DataService() {
        sensor = new SensorRead();
        sensor.start();
    }

    public Flux<Double> getTemp1Stream() {
        return Flux.<Double>generate(sink -> sink.next(sensor.getTemp1()))
                .delayElements(Duration.ofMillis(1000));
    }

    public Flux<Double> getTemp2Stream() {
        return Flux.<Double>generate(sink -> sink.next(sensor.getTemp2()))
                .delayElements(Duration.ofMillis(1000));
    }

    public Flux<Integer> getHumo1Stream() {
        return Flux.<Integer>generate(sink -> sink.next(sensor.getHumo1()))
                .delayElements(Duration.ofMillis(1000));
    }

    public Flux<Integer> getHumo2Stream() {
        return Flux.<Integer>generate(sink -> sink.next(sensor.getHumo2()))
                .delayElements(Duration.ofMillis(1000));
    }

    public Flux<Boolean> getFuegoStream() {
        return Flux.<Boolean>generate(sink -> sink.next(sensor.isFuego()))
                .delayElements(Duration.ofMillis(1000));
    }
}