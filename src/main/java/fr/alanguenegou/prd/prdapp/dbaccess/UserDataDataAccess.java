package fr.alanguenegou.prd.prdapp.dbaccess;

import fr.alanguenegou.prd.prdapp.graph.Graph;
import fr.alanguenegou.prd.prdapp.graph.Node;

import fr.alanguenegou.prd.prdapp.userdata.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The class managing the user data database access used to populate a {@link UserData} instance
 * @author GUENEGOU A.
 * @version 1.00
 */
public class UserDataDataAccess {

    /**
     * A logger instance to log infos in the console
     */
    private final static Logger log = LoggerFactory.getLogger(GraphDataAccess.class);

    /**
     * An instance of a JdbcTemplate used to connect to a database
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * The class constructor with database connexion
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
     * Prints the number of rows in the user data database
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
     * Populates the UserData instance according to the user data database
     * @param graph The {@link Graph} instance where to find sections details of Tours
     * @return The populated UserData instance
     */
    public UserData populateUserData(Graph graph) {
        UserData userData = new UserData();
        StopWatch watch = new StopWatch();
        watch.start();
        log.info("Début du remplissage de l'objet données utilisateur...");

        var sql = "SELECT id, routelink_id from traces_splitted_areaid_7 ORDER BY id";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);


        int numberOfUnknownSections = 0;
        ArrayList<Long> unkownSections = new ArrayList<>();
        int tempTripId = 0;
        long numberOfTrips = 0;

        // iterates through all rows of userDataDataSource
        for (Map<String, Object> row : rows) {

            Long routelinkId = (Long) row.get("routelink_id");

            // checks if all sections in user data are known in Tours data graph
            /*
            if (!graph.getSections().keySet().contains(routelinkId) && !unkownSections.contains(routelinkId)) {
                unkownSections.add(routelinkId);
                numberOfUnknownSections++;
            }
            */

            Node startNode = graph.getNodeStartBySection(routelinkId);
            Node endNode = graph.getNodeEndBySection(routelinkId);

            // if we operate on a new trip
            if ((int) row.get("id") != tempTripId) {

                numberOfTrips++;

                // updates trip id to the new one
                tempTripId = (int) row.get("id");

                // creates new trip and adds it to userData
                LinkedList<Node> trip = new LinkedList<>();
                userData.addTrip(tempTripId, trip);

            }

            // here operating on actual trip row

            // checks if start node is already in trip. If not, adds it
            if (userData.isNotInTrip(tempTripId, startNode)){
                userData.addNodeToTrip(tempTripId, startNode);
            }

            // checks if end node is already in trip. If not, adds it
            if (userData.isNotInTrip(tempTripId, endNode)){
                userData.addNodeToTrip(tempTripId, endNode);
            }

            // adds the routelink to the trip to keep track of all sections composing a trip
            userData.addSectionToTrip(tempTripId, routelinkId);

        }

        watch.stop();
        log.info("     Fin du remplissage de l'objet données utilisateur, effectué en {} secondes", watch.getTotalTimeSeconds());
        log.info("     Itération faite sur {} lignes", rows.size());
        log.info("     {} trajets ont été créés", numberOfTrips);

        //log.warn("     Attention : {} section(s) présente(s) dans les données utilisateur semblent non présentes dans le graph de Tours modélisé", numberOfUnknownSections);

        return userData;
    }
}