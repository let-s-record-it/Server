package com.sillim.recordit.config;

import com.sillim.recordit.config.neo4j.Neo4jRepo;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(
		entityManagerFactoryRef = "jpaEntityManagerFactory",
		transactionManagerRef = "jpaTransactionManager",
		excludeFilters =
				@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Neo4jRepo.class),
		basePackages = {"com.sillim.recordit"})
public class DBConfig {

	@Bean(name = "dataSource")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "jpaEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean containerEntityManagerFactory(
			EntityManagerFactoryBuilder builder, @Qualifier("dataSource") DataSource dataSource) {
		return builder.dataSource(dataSource).packages("com.sillim.recordit").build();
	}

	@Bean(name = "jpaTransactionManager")
	@Primary
	public JpaTransactionManager transactionManager(
			@Qualifier("jpaEntityManagerFactory") LocalContainerEntityManagerFactoryBean mfBean) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(mfBean.getObject());
		return transactionManager;
	}
}
