package dev.nkucherenko.redischat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisChatApplication {

    public static void main(final String[] args) {

        SpringApplication app = new SpringApplication(RedisChatApplication.class);
        app.setWebApplicationType(WebApplicationType.REACTIVE);
        app.run(args);
    }

}
