package io.github.marcusadriano.dolcegustobot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class DolcegustoBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(DolcegustoBotApplication.class, args);
    }

}
