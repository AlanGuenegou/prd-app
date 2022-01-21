package fr.alanguenegou.prd.prdapp.dbaccess;

import fr.alanguenegou.prd.prdapp.graph.Graph;
import fr.alanguenegou.prd.prdapp.graph.Node;
import fr.alanguenegou.prd.prdapp.userdata.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.StopWatch;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserDataDataAccess {

    private final static Logger log = LoggerFactory.getLogger(GraphDataAccess.class);

    private final JdbcTemplate jdbcTemplate;

    /**
     * constructor of a userDataDataAccess with database connexion
     */
    public UserDataDataAccess() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://localhost:5432/donnees_user");
        ds.setUsername("postgres");
        ds.setPassword("password");
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    /**
     * prints number of rows in the userData data source
     */
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

    /**
     * populates the userData object according to userData data source
     * @param graph the graph object where to find sections details of Tours
     * @return the userData object freshly populated
     */
    public UserData populateUserData(Graph graph) {
        UserData userData = new UserData();
        StopWatch watch = new StopWatch();
        watch.start();
        log.info("Début du remplissage de l'objet données utilisateur...");

        var sql = "SELECT id, routelink_id from traces_splitted_areaid_7 ORDER BY id";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        int tempId = 0;

        // iterates through all rows of userDataDataSource
        for (Map<String, Object> row : rows) {

            Node startNode = graph.getNodeStartBySection(((Long) row.get("routelink_id")).intValue());
            Node endNode = graph.getNodeEndBySection(((Long) row.get("routelink_id")).intValue());


            // if we operate on a new trip
            if ((int) row.get("id") != tempId) {

                // updates trip id to the new one
                tempId = (int) row.get("id");

                // creates new trip and adds it to userData
                List<Node> trip = new LinkedList<>();
                userData.addTrip(tempId, trip);

            }

            // here operating on actual trip row

            // checks if start node is already in trip. If not, adds it
            if (userData.isNotInTrip(tempId, startNode)){
                userData.addNodeToTrip(tempId, startNode);
            }

            // checks if end node is already in trip. If not, adds it
            if (userData.isNotInTrip(tempId, endNode)){
                userData.addNodeToTrip(tempId, endNode);
            }

        }

        watch.stop();
        log.info("Fin du remplissage de l'objet données utilisateur, effectué en {} secondes", watch.getTotalTimeSeconds());
        return userData;
    }
}