package fr.alanguenegou.prd.prdapp.dbconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class GraphDataSourceConfig {

    @Bean(name = "graphDataSource")
    @ConfigurationProperties(prefix = "app.datasource.graph")
    @Primary
    public DataSource graphDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "jdbcGraph")
    @Autowired
    public JdbcTemplate graphJdbcTemplate(@Qualifier("graphDataSource") DataSource graphDataSource) {
        return new JdbcTemplate(graphDataSource);
    }
}
