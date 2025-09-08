package com.sillim.recordit.config.neo4j;

import org.neo4j.driver.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableNeo4jRepositories(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Neo4jRepo.class), transactionManagerRef = "neo4jTransactionManager", basePackages = {
		"com.sillim.recordit"})
public class Neo4jConfig {

	// @Bean
	// Configuration cypherDslConfiguration() {
	// return Configuration.newConfig().withDialect(Dialect.NEO4J_5).build();
	// }

	@Bean
	public Neo4jTransactionManager neo4jTransactionManager(Driver driver, DatabaseSelectionProvider provider) {
		return new Neo4jTransactionManager(driver, provider);
	}
}
