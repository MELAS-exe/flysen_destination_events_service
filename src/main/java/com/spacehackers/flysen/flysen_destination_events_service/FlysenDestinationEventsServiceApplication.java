package com.spacehackers.flysen.flysen_destination_events_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class FlysenDestinationEventsServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(FlysenDestinationEventsServiceApplication.class);

        // Add an initializer to load .env before Spring beans
        app.addInitializers(new DotenvInitializer());
        app.run(args);
    }

}
