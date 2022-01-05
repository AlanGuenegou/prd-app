package fr.alanguenegou.prd.prdapp.dbconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class UserDataDataSourceConfig {

    @Bean(name = "userDataDataSource")
    @ConfigurationProperties(prefix = "app.datasource.userdata")
    public DataSource userDataDataSource() {
        return DataSourceBuilder.create()
                .username("postgres")
                .build();
    }
}
