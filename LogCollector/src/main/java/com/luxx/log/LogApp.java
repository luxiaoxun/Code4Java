package com.luxx.log;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.util.TimeZone;

@SpringBootApplication
@SpringBootConfiguration
public class LogApp {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication springApplication = new SpringApplication(LogApp.class);
        springApplication.setBannerMode(Banner.Mode.LOG);
        springApplication.run(args);
    }
}
