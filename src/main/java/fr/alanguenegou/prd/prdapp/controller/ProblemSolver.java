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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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


    // TODO sortir des stats (histogramme excel) sur la distribution des écarts (0 - 10% - 20% - etc)
    /**
     * solves the first problem of the project
     */
    private void solveFirstProblem() {
        Collection<Trip> trips = userData.getTrips().values();
        double globalDifference = 0.0;
        int[] distribution = new int[11];
        int profileIterator = 0;

        for (Trip trip : trips) {

            // TODO méthode compare trip qui fonctionne mal --> ça vient du calcul du plus court chemin ou de la méthode en elle même ?
            double difference = trip.compareTripWithCalculatedVersion(graph);
            globalDifference += difference;
            profileIterator++;
            if (Math.floorMod(profileIterator, 50) == 0)   {
                log.info("{} trajets ont été traités jusqu'à présent sur un total de {}",
                        profileIterator, userData.getTrips().size());
            }

            // fill distribution array
            if (difference >= 0.0 && difference < 10.0)
                distribution[0]++;
            else if (difference >= 10.0 && difference < 20.0)
                distribution[1]++;
            else if (difference >= 20.0 && difference < 30.0)
                distribution[2]++;
            else if (difference >= 30.0 && difference < 40.0)
                distribution[3]++;
            else if (difference >= 40.0 && difference < 50.0)
                distribution[4]++;
            else if (difference >= 50.0 && difference < 60.0)
                distribution[5]++;
            else if (difference >= 60.0 && difference < 70.0)
                distribution[6]++;
            else if (difference >= 70.0 && difference < 80.0)
                distribution[7]++;
            else if (difference >= 80.0 && difference < 90.0)
                distribution[8]++;
            else if (difference >= 90.0 && difference <= 100.0)
                distribution[9]++;
            else
                distribution[10]++;

        }

        globalDifference = globalDifference/trips.size();

        System.out.println("-----------------------------------------------------------------");
        System.out.println();
        System.out.format("De façon brute, les chemins calculés sont en moyenne %.2f%% différents du trajet réellement emprunté par l'utilisateur%n", globalDifference);
        System.out.format("Cette analyse est faite sur une base de %d trajets utilisateur%n", trips.size());
        System.out.format("Détails de la distribution des écarts (bornes en pourcentage) :%n");
        System.out.format("[0, 10[ [10, 20[ [20, 30[ [30, 40[ [40, 50[ [50, 60[ [60, 70[ [70, 80[ [80, 90[ [90, 100] -> ");
        for (int i = 0; i < distribution.length-1; i++) {
            System.out.print(distribution[i] + ", ");
        }
        System.out.println();
        System.out.format("Attention : %d trajets ont un écart supérieur à 100%%%n", distribution[10]);
        System.out.println();
        System.out.println("-----------------------------------------------------------------");

    }

    /**
     * solves the second problem of the project
     */
    private void solveSecondProblem() {
        // TODO trouver comment gérer la modification d'un graph (en gardant les mêmes objets ?)

        // TODO pour un tronçon modifié, trouver les trips qui passent à proximité en calculant la distance des trips au tronçon via sql request
        //  puis dans les fronts de pareto, déterminer les points à proximité
        //  puis  si au mini 1 point est modifié par la modification de sécurité DANS LA REGION qui intéresse utilisateur : calculer le changement en reprenant la méthode du PB1
    }


    // TODO changer le mot danger value en securityValue PARTOUT !
    /**
     * launches the resolution of the problem that user chose to solve
     */
    public void launchProblemSolving() {
        graph.printNumberOfNodesHavingOnePredecessorAndSuccessor();

        int initialUserDataSize = userData.getTrips().size();
        int[] checkTripsInfos = userData.checkTrips();
        //dialog.printTripsSizeStats(checkTripsInfos[2], checkTripsInfos[3]);
        dialog.printNumberOfUserDataNonValidTrips(checkTripsInfos[0], checkTripsInfos[1], initialUserDataSize, checkTripsInfos[4]);

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
