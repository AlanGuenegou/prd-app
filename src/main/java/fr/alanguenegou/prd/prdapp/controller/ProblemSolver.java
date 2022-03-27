package fr.alanguenegou.prd.prdapp.controller;

import fr.alanguenegou.prd.prdapp.dbaccess.GraphDataAccess;
import fr.alanguenegou.prd.prdapp.graph.Graph;
import fr.alanguenegou.prd.prdapp.userdata.Trip;
import fr.alanguenegou.prd.prdapp.userdata.UserData;
import fr.alanguenegou.prd.prdapp.view.Dialog;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The typical controller class of the app
 * @author GUENEGOU A.
 * @version 1.00
 */
public class ProblemSolver {

    /**
     * A logger instance to log infos in the console
     */
    private final static Logger log = LoggerFactory.getLogger(ProblemSolver.class);

    /**
     * An instance of the Graph class
     */
    @Getter @Setter
    private Graph graph;

    /**
     * An instance of the UserData class
     */
    @Getter @Setter
    private UserData userData;

    /**
     * An instance of the GraphDataAccess class
     */
    @Getter @Setter
    private GraphDataAccess graphDataAccess;

    /**
     * An instance of the Dialog class
     */
    @Getter @Setter
    private Dialog dialog = new Dialog();

    /**
     * The class constructor
     * @param graph The {@link Graph} instance that is going to be used
     * @param userData The {@link UserData} instance that is going to be used
     * @param graphDataAccess The instance of a {@link GraphDataAccess}
     */
    public ProblemSolver(Graph graph, UserData userData, GraphDataAccess graphDataAccess) {
        this.graph = graph;
        this.userData = userData;
        this.graphDataAccess = graphDataAccess;
    }


    // TODO sortir des stats (histogramme excel) sur la distribution des écarts (0 - 10% - 20% - etc)
    /**
     * Solves the first problem of this research and development work
     */
    private void solveFirstProblem() {
        Collection<Trip> trips = userData.getTrips().values();
        double globalDifference = 0.0;
        int[] distribution = new int[11];
        int profileIterator = 0;

        for (Trip trip : trips) {

            if (trip.getId() == 6094924) {}

            // TODO méthode compare trip qui fonctionne mal --> ça vient du calcul du plus court chemin ou de la méthode en elle même ?
            double difference = trip.compareTripWithCalculatedVersion(graph);
            globalDifference += difference;
            profileIterator++;
            if (Math.floorMod(profileIterator, 50) == 0)   {
                log.info("{}% -> {} trajets ont été traités jusqu'à présent sur un total de {}",
                        Math.round(((double)profileIterator)/userData.getTrips().size()*100), profileIterator, userData.getTrips().size());
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


        System.out.println("\nDétails de la distribution des écarts (bornes en pourcentage) :");
        System.out.println("[0, 10[   : " + distribution[0]);
        System.out.println("[10, 20[  : " + distribution[1]);
        System.out.println("[20, 30[  : " + distribution[2]);
        System.out.println("[30, 40[  : " + distribution[3]);
        System.out.println("[40, 50[  : " + distribution[4]);
        System.out.println("[50, 60[  : " + distribution[5]);
        System.out.println("[60, 70[  : " + distribution[6]);
        System.out.println("[70, 80[  : " + distribution[7]);
        System.out.println("[80, 90[  : " + distribution[8]);
        System.out.println("[90, 100] : " + distribution[9]);
        System.out.println();
        System.out.format("Attention : %d trajets ont un écart supérieur à 100%%%n", distribution[10]);
        System.out.println();
        System.out.println("-----------------------------------------------------------------");

    }


    /**
     * Solves the second problem of this research and development work
     */
    private void solveSecondProblem() {
        // TODO trouver comment gérer la modification d'un graph --> système boolean pour calcul shortest path
        //  --> 1 = initial secu value et 2 = alternative security value (if exists)
        //  IMPLEMENTED --> à confirmer

        // keeps track of the progress of the numerous iterations
        int profileIterator;

        /*
        // finds arbitrarily a section to modify
        Long[] sectionToModify = getMostTakenSection();
        log.info("Un section importante à modifier pourrait être... ");
        dialog.printSectionInformation(sectionToModify[0], sectionToModify[1], graph, userData.getTrips().size());

         */


        // modifies the section in the Tours graph
        HashMap<Long, Integer> sectionsToModify = new HashMap<>();
        sectionsToModify.put(8327829394L, 5);
        //sectionsToModify.put(sectionToModify[0], 5);
        graph.modifyGraph(sectionsToModify);



        int nbOfModifiedTrips = 0;
        double globalImprovement = 0;

        HashSet<Trip> involvedTrips = new HashSet<>(userData.getTrips().values());

        /*

        Récupérer tous les trajets proches du tronçon modifié c'est OK avec le code ci dessous mis en commentaire, mais c'est trop long pour pas grand chose

         */
        /*
        profileIterator = 0;
        log.info("Début de la phase de récupération des distances entre le tronçon modifié et les trajets utilisateur...");
        // TODO déterminer un "relevant radius"
        final double relevantRadius =  100; // Double.MAX_VALUE; // relevant distance between the modified section and a trip for the trip to be potentially involved
        for (Map.Entry<Integer, Trip> trip : userData.getTrips().entrySet())  {
            for (long sectionId : trip.getValue().getSections()) {
                // if the modified section is close enough to the trip
                if (graphDataAccess.retrieveDistanceBetweenTwoSections(sectionToModify, sectionId) <= relevantRadius) {
                    involvedTrips.add(trip.getValue());
                    break;
                }
            }

            profileIterator++;
            if (Math.floorMod(profileIterator, 50) == 0)   {
                log.info("{}% -> {} trajets jusqu'à présent ont vu leur proximité avec le tronçon modifié analysée, sur un total de {}",
                        profileIterator/userData.getTrips().size()*100, profileIterator, userData.getTrips().size());
            }

        }

         */


        profileIterator = 0;
        log.info("Début de la phase de calculs...");
        for (Trip trip : involvedTrips) {
            profileIterator++;

            // computes the initial pareto front and also deduce the weights used for the edge values of the trip
            HashMap<Double, Pair<Double, Double>> initialParetoFront = trip.setTripWeightsThanksToComparison(graph);

            graph.prepareNewCalculation();

            // computes the pareto front with the modified edge danger values
            HashMap<Double, Pair<Double, Double>> modifiedParetoFront = graph.calculateLabelsForManyLinearCombinations(trip.getStartNode(), trip.getEndNode(), Graph.WITH_ALTERNATIVE_DANGER_VALUE);


            // initialises a set containing possibly interesting labels in a pareto front
            // with distance weight of label as map key and its future percent variation from initial value to new value as map value
            HashMap<Double, Double> distanceWeightsNearUserTrip = new HashMap<>();
            double tripDistanceWeightValue = trip.getDeducedWeightsValues().getValue0();
            if (tripDistanceWeightValue == Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[0]) {

                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[0], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[1], 0.0);

            }
            else if (tripDistanceWeightValue == Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[1]) {

                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[0], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[1], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[2], 0.0);

            }
            else if (tripDistanceWeightValue == Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[2]) {

                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[1], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[2], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[3], 0.0);

            }
            else if (tripDistanceWeightValue == Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[3]) {

                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[2], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[3], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[4], 0.0);

            }
            else if (tripDistanceWeightValue == Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[4]) {

                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[3], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[4], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[5], 0.0);

            }
            else if (tripDistanceWeightValue == Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[5]) {

                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[4], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[5], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[6], 0.0);

            }
            else if (tripDistanceWeightValue == Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[6]) {

                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[5], 0.0);
                distanceWeightsNearUserTrip.put(Graph.LINEAR_COMBINATION_DISTANCE_WEIGHTS[6], 0.0);

            }

            // checks if there are modifications among labels near the user trip label one
            HashMap<Double, Double> tempModifications = new HashMap<>(distanceWeightsNearUserTrip.size());
            for (Double distanceWeight : distanceWeightsNearUserTrip.keySet()) {

                // if modified label has seen its value modified (= the graph modification  impacted the path for this linear combination of weights)
                if (!initialParetoFront.get(distanceWeight).equals(modifiedParetoFront.get(distanceWeight))) {

                    // computes percent variation and adds it in the previous hashmap
                    // TODO quelle norme utiliser pour orthonormer le calcul ?
                    double initialLabelCost = distanceWeight * initialParetoFront.get(distanceWeight).getValue0() / ((((1))))
                            + (1-distanceWeight) * initialParetoFront.get(distanceWeight).getValue1() / ((((1))));

                    double newLabelCost = distanceWeight * modifiedParetoFront.get(distanceWeight).getValue0() / ((((1))))
                            + (1-distanceWeight) * modifiedParetoFront.get(distanceWeight).getValue1() / ((((1))));


                    tempModifications.put(distanceWeight, ((newLabelCost - initialLabelCost) / initialLabelCost * 100));

                    // log.warn("initialLabelCost = {} , newLabelCost = {} , iteration n°{}", initialLabelCost, newLabelCost, profileIterator);

                }
                else {
                    tempModifications.put(distanceWeight, -999.0);
                }
            }

            // effectively proceeds to make the changes
            for (Map.Entry<Double, Double> modification : tempModifications.entrySet()) {
                if (modification.getValue() == -999.0) {
                    distanceWeightsNearUserTrip.remove(modification.getKey());
                } else {
                    distanceWeightsNearUserTrip.replace(modification.getKey(), modification.getValue());
                }
            }

            if (distanceWeightsNearUserTrip.size() > 0) {
                nbOfModifiedTrips++;
                globalImprovement += Collections.min(distanceWeightsNearUserTrip.values());
            }


            if (Math.floorMod(profileIterator, 50) == 0)   {
                log.info("{}% -> {} trajets ont été traités jusqu'à présent sur un total de {}",
                        Math.round(((double)profileIterator)/involvedTrips.size()*100), profileIterator, involvedTrips.size());
            }

            /*
            PROBLEME DE CETTE RESOLUTION : on part du principe que le trajet calculé correspond à celui véritablement emprunté par l'utilisateur...
             */


        }

        globalImprovement = globalImprovement / nbOfModifiedTrips;

        System.out.println("-----------------------------------------------------------------");
        System.out.println();
        System.out.format("Sur %d trajets utilisateur recensés, %d seraient impactés par cette modification de tronçon.%n", userData.getTrips().size(), nbOfModifiedTrips);
        System.out.format("Dans le cadre de ces %d trajets, la baisse moyenne du coût du trajet serait de %.3f%%.%n", nbOfModifiedTrips, globalImprovement);

        System.out.println();
        System.out.println("-----------------------------------------------------------------");



    }


    // TODO changer le mot SECU VERS DANGER PARTOUT !
    /**
     * Launches the resolution of the problem that user chose to solve
     */
    public void launchProblemSolving() {
        //graph.printNumberOfNodesHavingOnePredecessorAndSuccessor();

        System.out.println("------------------------- FILTRAGE DES DONNEES UTILISATEUR N°1 ------------------------- \n");
        int initialUserDataSize = userData.getTrips().size();
        int[] checkTripsInfos = userData.checkTrips();
        dialog.printNumberOfUserDataNonValidTrips(checkTripsInfos[0], checkTripsInfos[1], initialUserDataSize, checkTripsInfos[2]);
        System.out.println("------------------------- FILTRAGE DES DONNEES UTILISATEUR N°2 ------------------------- \n");
        userData.getTripsDistancesAndRemoveThoseUnderXMeters(500);
        System.out.println("---------------------------------------------------------------------------------------- \n");


        switch (dialog.getProblemChoice()) {
            case 1:
                solveFirstProblem();
                break;
            case 2:
                solveSecondProblem();
                break;
            case 3:
                // solveThirdProblem();
                break;
        }
    }

    /**
     * Iterates through all user data and finds the most taken section in Tours graph
     * @return The ID of the most taken section and its frequency
     */
    public Long[] getMostTakenSection() {
        // TODO réécrire la fonction getMostTakenSection pour vérifier que la section renvoyée n'a pas déjà un ratio 5 en sécurité
        //  + getMostTakenSection() récupère potentiellement section très courte ? atm : distance = 7  pour la most taken section



        // populates a map linking each section of Tours graph to its frequency of use in user data
        HashMap<Long, Integer> sectionsUseFrequency = new HashMap<>(graph.getSections().size(), 1f);
        for(Map.Entry<Long, Pair<Long, Long>> section : graph.getSections().entrySet()) {
            int frequency = 0;

            // iterates through every trip of user data
            for (Trip trip : userData.getTrips().values()) {

                // if the section is in the trip, increase the frequency value by 1
                if (trip.sectionIsInTrip(graph.getNodeStartBySection(section.getKey()), graph.getNodeEndBySection(section.getKey()))) {
                    frequency++;
                }
            }
            sectionsUseFrequency.put(section.getKey(), frequency);
        }

        int maxFrequency = Collections.max(sectionsUseFrequency.values());

        for (Map.Entry<Long, Integer> sectionUseFrequency : sectionsUseFrequency.entrySet()) {
            if (sectionUseFrequency.getValue().equals(maxFrequency))
                return new Long[] {sectionUseFrequency.getKey(), (long) maxFrequency};
        }

        return null;
    }

}
