package com.sillim.recordit.support.repository;

import com.sillim.recordit.support.integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.mysql.MySQLContainer;

@IntegrationTest
@EnableJpaAuditing
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RepositoryTest {

	static MySQLContainer container =
			new MySQLContainer("mysql:8.0.44-debian")
					.withDatabaseName("didim")
					.withUsername("test")
					.withPassword("test");
	@Autowired protected TestEntityManager em;

	static {
		container.start();
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", container::getJdbcUrl);
		registry.add("spring.datasource.username", container::getUsername);
		registry.add("spring.datasource.password", container::getPassword);
	}

	@BeforeEach
	void initData() {
		em.flush();
		em.clear();
	}
}
