package org.ascaso.webfluxtaskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class WebFluxTaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebFluxTaskManagerApplication.class, args);
    }

}
