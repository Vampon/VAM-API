package com.vampon.vamapiclientsdk;

import com.vampon.vamapiclientsdk.client.VamApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("vamapi.client")
@Data
//@ComponentScan
public class VamApiClientConfig {
    private String accessKey;
    private String secretKey;
    @Bean
    public VamApiClient vamApiClient() {
        return new VamApiClient(accessKey, secretKey);
    }

}
