plugins {
	java
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
	id("jacoco")
}

fun getGitHash(): String {
	return providers.exec {
		commandLine("git", "rev-parse", "--short", "HEAD")
	}.standardOutput.asText.get().trim()
}

group = "kr.hhplus.be"
version = getGitHash()

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
	}
}

dependencies {
    // Spring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

    // DB
	runtimeOnly("com.mysql:mysql-connector-j")
	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
	// swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
	// webflux
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	// redisson
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.redisson:redisson-spring-boot-starter:3.17.6")
	// Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mysql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
	systemProperty("user.timezone", "UTC")
	finalizedBy(tasks.jacocoTestReport)
}

jacoco {
	toolVersion = "0.8.8" // ✅ 최신 JaCoCo 버전 설정
}

tasks.jacocoTestReport {
	dependsOn(tasks.test) // ✅ 테스트 실행 후 커버리지 리포트 생성
	reports {
		xml.required.set(true)
		csv.required.set(false)
		html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml")) // ✅ HTML 리포트 경로 설정
	}
}
tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			limit {
				minimum = BigDecimal("0.60") // ✅ BigDecimal 값을 직접 할당
			}
		}
	}
}
tasks.named("check") {
	dependsOn(tasks.jacocoTestCoverageVerification) // ✅ 커버리지 검증 포함
}
