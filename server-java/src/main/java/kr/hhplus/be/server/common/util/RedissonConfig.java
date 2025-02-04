package kr.hhplus.be.server.common.util;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

//    @Bean
//    public RedissonClient redissonClient() {
//        Config config = new Config();
//        config.useSingleServer().setAddress("redis://127.0.0.1:6379"); // Redis 서버 주소 설정
//        return Redisson.create(config);
//    }

    @Value("${spring.data.redis.host}")  // ✅ @DynamicPropertySource에서 설정한 값 사용
    private String redisHost;

    @Value("${spring.data.redis.port}")  // ✅ @DynamicPropertySource에서 설정한 포트 사용
    private int redisPort;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String redisUrl = "redis://" + redisHost + ":" + redisPort;  // ✅ 동적 값 적용
        System.out.println("🔹 Redisson connecting to: " + redisUrl);

        config.useSingleServer().setAddress(redisUrl);

        return Redisson.create(config);
    }
}
