package ru.nstu.exam.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Value("${spring.datasource.url}")
    private String businessJdbcUrl;
    @Value("${spring.datasource.username}")
    private String businessJdbcUsername;
    @Value("${spring.datasource.password}")
    private String businessJdbcPassword;
    @Value("${spring.datasource.pool-size:10}")
    private int businessPoolSize;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig configuration = new HikariConfig();
        configuration.setJdbcUrl(businessJdbcUrl);
        configuration.setUsername(businessJdbcUsername);
        configuration.setPassword(businessJdbcPassword);
        configuration.setMaximumPoolSize(businessPoolSize);

        return new HikariDataSource(configuration);
    }
}
