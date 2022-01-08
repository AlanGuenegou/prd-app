package fr.alanguenegou.prd.prdapp.dbaccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class UserDataDataAccess {

    private final static Logger log = LoggerFactory.getLogger(GraphDataAccess.class);

    private final JdbcTemplate jdbcTemplate;

    public UserDataDataAccess() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://localhost:5432/donnees_user");
        ds.setUsername("postgres");
        ds.setPassword("password");
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    public void printNumOfRows() {

        var sql = "SELECT COUNT(*) FROM traces_splitted_areaid_7";
        try {
            Integer numOfRows = jdbcTemplate.queryForObject(sql, Integer.class);
            log.info("La connexion à la base de données du graphe a fonctionné");
            System.out.format("Il y a %d lignes dans les données utilisateur mises à ma disposition%n", numOfRows);

        } catch (NullPointerException nullPointerException) {
            System.out.println("La connexion à la base de données du graphe a échoué :");
            nullPointerException.printStackTrace();
        }

    }
}