Demo Spring Boot với Testcontainers và PostgreSQL

1. Giới thiệu

Dự án này minh họa cách sử dụng Testcontainers để kiểm thử ứng dụng Spring Boot với PostgreSQL mà không cần thiết lập cơ sở dữ liệu cục bộ.

2. Công nghệ sử dụng

Spring Boot 3.x

Spring Data JPA

PostgreSQL

Testcontainers

JUnit 5

3. Cài đặt

3.1. Yêu cầu hệ thống

JDK 17+ (Java 23 được hỗ trợ)

Docker (bắt buộc để chạy Testcontainers)

Gradle (nếu build thủ công)

3.2. Cấu hình project

Thêm các dependencies vào build.gradle:

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.postgresql:postgresql'

    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.boot:spring-boot-testcontainers'
    testImplementation 'org.testcontainers:postgresql'
    testImplementation 'org.testcontainers:junit-jupiter'

    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

3.3. Cấu hình Testcontainers

Tạo BaseTestContainer.java:

package com.example.demo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseTestContainer {
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void startContainer() {
        postgres.start();
    }

    @AfterAll
    static void stopContainer() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}

3.4. Cấu hình application.properties

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
logging.level.org.springframework=INFO
logging.level.org.hibernate.SQL=DEBUG

🚀 Lưu ý: Không cần khai báo spring.datasource.url vì Testcontainers tự động cung cấp giá trị này.

3.5. Viết Test Controller

Ví dụ UserControllerTest.java:

package com.example.demo.controller;

import com.example.demo.BaseTestContainer;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends BaseTestContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testGetUserById() throws Exception {
        User user = new User(null, "Test User", "test@example.com");
        user = userRepository.save(user);

        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk());
    }
}

4. Chạy kiểm thử

./gradlew test

Hoặc trên Windows:

gradlew test

5. Điểm khác biệt giữa hai cách cấu hình Testcontainers

Cách 1: Dùng jdbc:tc:postgresql:15-alpine://

spring.datasource.url=jdbc:tc:postgresql:15-alpine:///
spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update

✔ Ưu điểm: Không cần viết BaseTestContainer.java, Spring Boot tự động nhận diện container PostgreSQL.

✖ Nhược điểm: Không kiểm soát được lifecycle của Testcontainers.

Cách 2: Dùng BaseTestContainer.java

Ưu điểm:

Kiểm soát được thời gian start/stop của container.

Dùng chung cho nhiều test class.

Tuỳ chỉnh được thông số của PostgreSQL.

Nhược điểm: Cần thêm code setup.

✅ Khuyến nghị: Dùng BaseTestContainer.java nếu cần kiểm soát test tốt hơn!

6. Lưu ý quan trọng

Testcontainers cần Docker để chạy. Nếu lỗi Cannot resolve class 'ContainerDatabaseDriver', hãy kiểm tra:

docker ps -a

Nếu không thấy container, hãy kiểm tra Docker đã bật chưa.

Nếu dùng WSL trên Windows, hãy đảm bảo Docker chạy trong WSL mode.

Nếu gặp lỗi PersistenceException, kiểm tra build.gradle đã có:

testImplementation 'org.testcontainers:junit-jupiter'

Nếu chạy test mà PostgreSQL container không chạy, hãy thêm:

spring.datasource.url=jdbc:tc:postgresql:15-alpine:///

🎯 Vậy là bạn đã có một bộ test hoàn chỉnh với Testcontainers cho Spring Boot! 🚀

