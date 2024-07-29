package com.vampon.vamapigateway.config;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redisson")
@Data
public class RedissonConfig {

    private Integer database;

    private String host;

    private Integer port;

//    private String password;

    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建配置
        Config config = new Config();
        config.useSingleServer()
                .setDatabase(database)
                .setAddress("redis://" + host + ":" + port);
        // 2. 创建实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}

//@Configuration
//@ConfigurationProperties(prefix = "spring.redisson")
//@Data
//public class RedissonConfig {
//
//    private String host;
//
//    private String port;
//
//    @Bean
//    public RedissonClient redissonClient() {
//        // 1. 创建配置
//        Config config = new Config();
//        String redisAddress = String.format("redis://%s:%s", host, port);
//        config.useSingleServer().setAddress(redisAddress).setDatabase(4);
//        // 2. 创建实例
//        return Redisson.create(config);
//    }
//}