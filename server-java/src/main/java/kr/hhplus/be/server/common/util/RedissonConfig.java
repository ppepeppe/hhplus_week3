package kr.hhplus.be.server.common.util;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 🔹 키를 String 타입으로 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // 🔹 값을 JSON 형태로 직렬화
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }
}
