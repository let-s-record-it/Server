package com.sillim.recordit.config.neo4j;

import lombok.RequiredArgsConstructor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("local")
public class LocalNeo4jInitializer implements ApplicationRunner {

	private final Driver neo4jDriver;

	@Override
	public void run(ApplicationArguments args) {
		try (Session session = neo4jDriver.session()) {
			session.run("MATCH (n) DETACH DELETE n");
		}
	}
}
