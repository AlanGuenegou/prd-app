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
public class UserDataDataSourceConfig {

    @Bean(name = "userDataDataSource")
    @ConfigurationProperties(prefix = "app.datasource.userdata")
    public DataSource userDataDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "jdbcUserData")
    @Autowired
    public JdbcTemplate userDataJdbcTemplate(@Qualifier("userDataDataSource") DataSource userDataDataSource) {
        return new JdbcTemplate(userDataDataSource);
    }
}
