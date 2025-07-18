package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.practicum.shareit")
public class ShareItGateway {

    public static void main(String[] args) {
        SpringApplication.run(ShareItGateway.class, args);
    }
}
