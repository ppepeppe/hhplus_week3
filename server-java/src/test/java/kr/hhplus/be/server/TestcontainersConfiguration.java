package kr.hhplus.be.server;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;


@Configuration
class TestcontainersConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(TestcontainersConfiguration.class);
	public static final MySQLContainer<?> MYSQL_CONTAINER;
	public static final GenericContainer<?> REDIS_CONTAINER;

	static {
		// ✅ MySQL 컨테이너 설정
		MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
				.withDatabaseName("hhplus")
				.withUsername("root")
				.withPassword("1234")
				.waitingFor(Wait.forListeningPort());  // ✅ MySQL이 실행될 때까지 대기
		MYSQL_CONTAINER.start();

		System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
		System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
		System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());
		System.out.println("MYSQL container started");
		// ✅ Redis 컨테이너 설정
		REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.4.2"))
				.withExposedPorts(6379)
				.waitingFor(Wait.forListeningPort());  // ✅ Redis가 실행될 때까지 대기
		REDIS_CONTAINER.start();
		// ✅ System.setProperty()로 Redis 설정 추가
		System.setProperty("spring.data.redis.host", REDIS_CONTAINER.getHost());
		System.setProperty("spring.data.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());
	}

	@DynamicPropertySource
	static void registerRedisProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
		registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
	}

	@PreDestroy
	public void preDestroy() {
		if (MYSQL_CONTAINER.isRunning()) {
			MYSQL_CONTAINER.stop();
		}
		if (REDIS_CONTAINER.isRunning()) {
			REDIS_CONTAINER.stop();
		}
	}
}
