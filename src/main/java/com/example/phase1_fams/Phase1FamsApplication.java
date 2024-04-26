package com.example.phase1_fams;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.example.phase1_fams")
@EnableScheduling
public class Phase1FamsApplication {
    public static void main(String[] args) {
        SpringApplication.run(Phase1FamsApplication.class, args);
    }
}
