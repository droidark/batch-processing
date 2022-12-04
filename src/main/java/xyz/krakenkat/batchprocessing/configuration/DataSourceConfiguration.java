package xyz.krakenkat.batchprocessing.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:application.yml")
public class DataSourceConfiguration {

    @Primary
    @Bean(name = "h2DataSource")
    @ConfigurationProperties(prefix = "batch-execution.datasource.h2")
    public DataSource h2DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "postgresSQLDataSource")
    @ConfigurationProperties(prefix = "batch-execution.datasource.postgres")
    public DataSource postgresSQLDataSource() {
        return DataSourceBuilder.create().build();
    }
}
