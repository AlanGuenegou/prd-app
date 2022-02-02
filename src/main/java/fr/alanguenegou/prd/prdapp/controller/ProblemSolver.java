package fr.alanguenegou.prd.prdapp.controller;

import fr.alanguenegou.prd.prdapp.dbaccess.GraphDataAccess;
import fr.alanguenegou.prd.prdapp.graph.Graph;
import fr.alanguenegou.prd.prdapp.userdata.Trip;
import fr.alanguenegou.prd.prdapp.userdata.UserData;
import fr.alanguenegou.prd.prdapp.view.Dialog;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class ProblemSolver {

    private final static Logger log = LoggerFactory.getLogger(ProblemSolver.class);

    @Getter @Setter
    private Graph graph;

    @Getter @Setter
    private UserData userData;

    @Getter @Setter
    private Dialog dialog = new Dialog();

    /**
     * constructor of a ProblemSolver
     * @param graph graph instance that is going to be used
     * @param userData userData instance that is going to be used
     */
    public ProblemSolver(Graph graph, UserData userData) {
        this.graph = graph;
        this.userData = userData;
    }

    /**
     * solves the first problem of the project
     */
    private void solveFirstProblem() {
        Collection<Trip> trips = userData.getTrips().values();
        Double globalDifference = 0.0;
        int profileIterator = 0;

        for (Trip trip : trips) {
            globalDifference += trip.compareTripWithCalculatedVersion(graph);
            profileIterator++;
            if (Math.floorMod(profileIterator, 50) == 0)   {
                log.info("{} trajets ont été traités jusqu'à présent sur un total de {}",
                        profileIterator, userData.getTrips().size());
            }
        }

        globalDifference = globalDifference/trips.size();

        System.out.println("-----------------------------------------------------------------");
        System.out.println();
        System.out.format("De façon brute, les chemins calculés sont en moyenne %.2f%% différents du trajet réellement emprunté par l'utilisateur%n", globalDifference*100);
        System.out.format("Cette analyse est faite sur une base de %d trajets utilisateur%n", trips.size());
        System.out.println();
        System.out.println("-----------------------------------------------------------------");

    }

    /**
     * solves the second problem of the project
     */
    private void solveSecondProblem() {
        Graph modifiedGraph = this.graph;

    }

    /**
     * launches the resolution of the problem that user chose to solve
     */
    public void launchProblemSolving() {
        int initialUserDataSize = userData.getTrips().size();
        int numberOfUserDataNonValidTrips = userData.checkTrips();
        dialog.printNumberOfUserDataNonValidTrips(numberOfUserDataNonValidTrips, initialUserDataSize);

        switch (dialog.getProblemChoice()) {
            case 1:
                solveFirstProblem();
                break;
            case 2:
                // solveSecondProblem();
                break;
            case 3:
                // solveThirdProblem();
                break;
        }
    }

}
