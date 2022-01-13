package fr.alanguenegou.prd.prdapp.dbaccess;



import fr.alanguenegou.prd.prdapp.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.List;
import java.util.Map;

public class GraphDataAccess {

    private final static Logger log = LoggerFactory.getLogger(GraphDataAccess.class);

    private final JdbcTemplate jdbcTemplate;

    public GraphDataAccess() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://localhost:5432/graphe_tours_maj_securite_troncon");
        ds.setUsername("postgres");
        ds.setPassword("password");
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    public void printNumOfRows() {

        var sql = "SELECT COUNT(*) FROM link_geometry_areaid_7_amenagement";
        try {
            Integer numOfRows = jdbcTemplate.queryForObject(sql, Integer.class);
            log.info("La connexion à la base de données du graphe a fonctionné");
            System.out.format("Il y a %d lignes dans les données du graphe de Tours mises à ma disposition%n", numOfRows);

        } catch (NullPointerException nullPointerException) {
            log.error("La connexion à la base de données du graphe a échoué");
            nullPointerException.printStackTrace();
        }

    }

    public Integer getDangerValue(String layout) {
        int result;
        switch (layout) {
            case "test":
                result = 5;
                break;
            case "test2":
                result = 6;
                break;
            default:
                log.error("Le type d'aménagement n'est pas reconnu");
                result = Integer.MAX_VALUE;
                break;
        }
        return result;
    }

    public void populateGraph() {
        Graph graph = new Graph();

        var sql = "SELECT *, st_length(geometry) FROM link_geometry_areaid_7_amenagement";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        // iterates through all rows of graphDataSource
        for (Map<String, Object> row : rows) {

            // checks if nodeStart is already in graph. If not, adds it
            int nodeStart = (int)row.get("node_start");
            if (graph.isNotInGraph(nodeStart)){
                graph.addNode(nodeStart);
            }

            // checks if nodeEnd is already in graph. If not, adds it
            int nodeEnd = (int)row.get("node_end");
            if (graph.isNotInGraph(nodeEnd)){
                graph.addNode(nodeEnd);
            }

            // converts amenagement string into security value
            Integer security = getDangerValue((String)row.get("amenagement"));


            // TODO vérifier quelle unité est le st_length (en km ? miles ?)
            // adds NodeEnd as nodeStart neighbour with proper distance and security
            // TODO implémenter système de CL sécurité + distance
            graph.getNodes().get(nodeStart).addNeighbour(
                    graph.getNodes().get(nodeEnd),
                    (int)row.get("st_length"),
                    security);
        }

    }
}
