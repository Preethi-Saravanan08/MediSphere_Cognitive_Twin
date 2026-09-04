package com.medisphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.medisphere.repository")
@EnableAsync
@EnableScheduling
public class MediSphereApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediSphereApplication.class, args);
    }

}
