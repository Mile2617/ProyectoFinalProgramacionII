package com.example.application.threads;

import java.util.Scanner;

public class SensorRead extends Thread {


    private double value=0;

    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            // Simulate sensor data generation
            //value = Math.random() * 100; // Random value for demonstration
            try {
                value = sc.nextDouble(); // Read value from console input
                //Thread.sleep(1000); // Sleep for 1 second before generating next value
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public double getValue() {
        return value;
    }
}
