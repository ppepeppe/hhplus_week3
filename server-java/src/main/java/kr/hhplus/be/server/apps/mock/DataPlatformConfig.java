package kr.hhplus.be.server.apps.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DataPlatformConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}