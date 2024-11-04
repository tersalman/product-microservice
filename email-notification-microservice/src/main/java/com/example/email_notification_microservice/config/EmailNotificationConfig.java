package com.example.email_notification_microservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EmailNotificationConfig {

    @Bean
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
