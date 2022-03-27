package fr.alanguenegou.prd.prdapp;

import fr.alanguenegou.prd.prdapp.controller.ProblemSolver;
import fr.alanguenegou.prd.prdapp.dbaccess.GraphDataAccess;
import fr.alanguenegou.prd.prdapp.dbaccess.UserDataDataAccess;
import fr.alanguenegou.prd.prdapp.graph.Graph;
import fr.alanguenegou.prd.prdapp.userdata.UserData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * The launcher of this application
 * @author GUENEGOU A.
 * @version 1.00
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class PrdappApplication {

    /**
     * The classic main method launching the app
     * @param args null
     */
    public static void main(String[] args) {
        SpringApplication.run(PrdappApplication.class, args);

        // connects to GraphDataSource and populates the graph object
        GraphDataAccess graphDataAccess = new GraphDataAccess();
        Graph graph = graphDataAccess.populateGraph();

        // connects to UserDataDataSource and populates the userData object
        UserDataDataAccess userDataDataAccess = new UserDataDataAccess();
        UserData userData = userDataDataAccess.populateUserData(graph);

        // creates a problem solver instance that controls the app dialog
        ProblemSolver problemSolver = new ProblemSolver(graph, userData, graphDataAccess);
        problemSolver.launchProblemSolving();

    }
}
