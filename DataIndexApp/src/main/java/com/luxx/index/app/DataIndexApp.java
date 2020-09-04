package com.luxx.index.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@SpringBootConfiguration
@ComponentScan(basePackages = "com.luxx.index")
public class DataIndexApp {
    public static void main(String[] args) {
        SpringApplication.run(DataIndexApp.class, args);
    }
}
