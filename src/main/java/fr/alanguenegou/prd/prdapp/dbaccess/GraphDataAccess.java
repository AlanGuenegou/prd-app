package fr.alanguenegou.prd.prdapp.dbaccess;

import fr.alanguenegou.prd.prdapp.dbconfig.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@SpringBootApplication(scanBasePackages = {"fr.alanguenegou.prd.prdapp"})
public class GraphDataAccess implements CommandLineRunner {

    private final static Logger log = LoggerFactory.getLogger(GraphDataAccess.class);

    @Autowired
    @Qualifier("jdbcGraph")
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... strings) throws Exception {
        // code où on  vient récupérer les données graph ??
        //jdbcTemplate.query();
    }
}
