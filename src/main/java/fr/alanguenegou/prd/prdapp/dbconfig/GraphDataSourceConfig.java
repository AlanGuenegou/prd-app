package fr.alanguenegou.prd.prdapp.dbconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class GraphDataSourceConfig {

    @Bean(name = "graphDataSource")
    @ConfigurationProperties(prefix = "app.datasource.graph")
    @Primary
    public DataSource graphDataSource() {
        return DataSourceBuilder.create().build();
    }
}
