package fr.alanguenegou.prd.prdapp.dbaccess;



import fr.alanguenegou.prd.prdapp.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Map;

public class GraphDataAccess {

    private final static Logger log = LoggerFactory.getLogger(GraphDataAccess.class);

    private final JdbcTemplate jdbcTemplate;

    /**
     * constructor of GraphDataAccess witch connexion to graph data source and initialization of a jdbcTemplate
     */
    public GraphDataAccess() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://localhost:5432/graphe_tours_maj_securite_troncon");
        ds.setUsername("postgres");
        ds.setPassword("password");
        this.jdbcTemplate = new JdbcTemplate(ds);
    }

    /**
     * prints number of rows in the graph data source
     */
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

    /**
     * computes danger value from layout type and section length
     * @param layout layout type of the section
     * @param length length of the section
     * @return danger value
     */
    public Double getDangerValue(String layout, Double length) {
        int coefficient;
        if (layout == null) {
            coefficient = 1;
        }
        else {
            switch (layout) {
                case "Autres_chemins_piéton_autorisé_aux_vélos-1x":
                case "Autres_chemins_piéton_autorisé_aux_vélos-2x":
                case "Bandes_cyclables-1xD":
                case "Bandes_cyclables-1xG":
                case "Bandes_cyclables-2x":
                case "Bandes_cyclables-2xG":
                case "Footway_path_designated-1x":
                case "Footway_path_designated-2x":
                case "footway_permissive-1x":
                case "footway_permissive-2x":
                case "Trottoirs_cyclables-1x":
                case "Trottoirs_cyclables-2x":
                case "Voies_bus-1xD":
                case "Voies_bus-1xG":
                case "Voies_bus-2x":
                case "Zones_rencontre-1x":
                case "Zones_rencontre-2x":
                    coefficient = 3;
                    break;

                case "chaucidou":
                case "Cheminements_cyclables-1xD":
                case "Cheminements_cyclables-2x":
                case "Doubles-sens_cyclables_en_bande-G":
                case "Limite_a_30-1x":
                case "Limite_a_30-2x":
                case "Pedestrian_1x":
                case "Pedestrian_2x":
                case "Routes_services_chemins_agricoles-2x":
                case "Zones_30-1x":
                case "Zones_30-2x":
                    coefficient = 2;
                    break;

                case "Double-sens_cyclables_sans_bande":
                case "escalier-2x":
                    coefficient = 1;
                    break;

                case "Pistes_cyclables-1xD":
                case "Pistes_cyclables-2x":
                case "Pistes_cyclables-2xD":
                case "Pistes_cyclables-2xG":
                case "Pistes_sur_Trottoirs-1x":
                case "Pistes_sur_Trottoirs-2x":
                case "Voies_vertes-1x":
                case "Voies_vertes-2x":
                    coefficient = 5;
                    break;

                default:
                    log.error("Le type d'aménagement n'est pas reconnu");
                    coefficient = Integer.MAX_VALUE;
                    break;
            }
        }
        return length/coefficient;
    }

    /**
     * populates a new graph object according to graphDataSource
     * @return the graph freshly populated
     */
    public Graph populateGraph() {
        Graph graph = new Graph();
        StopWatch watch = new StopWatch();
        watch.start();
        log.info("Début du remplissage de l'objet graphe de Tours...");

        // geometry field is cast in geography to have length in meter and not in degree
        // TODO vérifier qu'effectivement c'est initialement rendu en degré
        //  et j'ai besoin de cast en geography pour avoir des mètres (demander à Mr Sauvanet ?)
        var sql = "SELECT *, st_length(geometry::geography) FROM link_geometry_areaid_7_amenagement";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        int numberOfNodes = 0; // number of nodes created
        // iterates through all rows of graphDataSource
        for (Map<String, Object> row : rows) {
            Double length = (Double)row.get("st_length");

            // checks if nodeStart is already in graph. If not, adds it
            long nodeStart = ((Long) row.get("node_start"));
            if (graph.isNotInGraph(nodeStart)){
                numberOfNodes++;
                graph.addNode(nodeStart);
            }

            // checks if nodeEnd is already in graph. If not, adds it
            long nodeEnd = ((Long) row.get("node_end"));
            if (graph.isNotInGraph(nodeEnd)){
                numberOfNodes++;
                graph.addNode(nodeEnd);
            }

            // populates sections Map
            graph.addSection(nodeStart, nodeEnd, ((Long) row.get("routelink_id")));

            // converts amenagement string into danger value
            Double danger = getDangerValue((String)row.get("amenagement"), length);

            // increments the predecessor number of nodeEnd, to keep track of predecessor numbers of nodes
            graph.getNodes().get(nodeEnd).incrementPredecessorNumber();

            // adds NodeEnd as nodeStart neighbour with proper distance and danger
            graph.getNodes().get(nodeStart).addNeighbour(
                    graph.getNodes().get(nodeEnd), length, danger);
        }

        watch.stop();
        log.info("     Fin du remplissage de l'objet graphe de Tours, effectué en {} secondes", watch.getTotalTimeSeconds());
        log.info("     Itération faite sur {} lignes", rows.size());
        log.info("     {} noeuds ont été créés", numberOfNodes);
        return graph;
    }
}
