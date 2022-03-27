package fr.alanguenegou.prd.prdapp.view;

import fr.alanguenegou.prd.prdapp.graph.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The class containing most of the methods designed for dialing with the app user
 * @author GUENEGOU A.
 * @version 1.00
 */
public class Dialog {
// TODO déplacer toutes les parties de print ici ?


    /**
     * Dialogs with user to retrieve the problem choice
     * @return The problem number asked to be solved
     */
    public int getProblemChoice() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Veuillez rentrer le numéro du problème dont vous voulez lancer la résolution : ");
        System.out.println("     1 : Les cyclistes de Tours respectent-ils les itinéraires qui leur sont conseillés ?");
        System.out.println("     2 : Évaluer l'impact d'une modification de tronçon dans la ville de Tours");
        System.out.println("     3 : Proposer activement une liste de modifications de tronçons " +
                "améliorant la qualité de vie des cyclistes au maximum, étant donné un budget");
        boolean continueInput = true;
        int choice = 0;
        ArrayList<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(2);
        choices.add(3);
        do {
            try {
                choice = sc.nextInt();
                if (choices.contains(choice)) {
                    System.out.println("Lancement de la résolution du problème " + choice + "...");
                    continueInput = false;
                } else {
                    System.out.println("Il est nécessaire de rentrer les valeurs 1, 2 ou 3. " +
                            "Veuillez rentrer une valeur à nouveau :");
                    sc.nextLine();
                }
            } catch (InputMismatchException e) {
                System.out.println("Il est nécessaire de rentrer les valeurs 1, 2 ou 3. " +
                        "Veuillez rentrer une valeur à nouveau :");
                sc.nextLine();
            }
        } while (continueInput);
        return choice;
    }


    /**
     * Prints the amount of non-valid trips in the regular UserData instance
     * @param numberOfNonValidTrips The amount of non-valid trips
     * @param numberOfTrips The total amount of trips in the UserData instance
     * @param numberOfProblematicNodesAtExtremities The amount of non-valid trips that have a problem with extremities nodes
     * @param extremitiesNodeNumber The number of nodes that can be called "extremities" in a trip (ex : 6 firsts and 6 lasts)
     */
    public void printNumberOfUserDataNonValidTrips (int numberOfNonValidTrips, int numberOfProblematicNodesAtExtremities, int numberOfTrips, int extremitiesNodeNumber) {
        System.out.format("Dans les données utilisateur, on retrouve un total de %d trajets non exploitables " +
                "car non conformes à la topologie du graphe de Tours%n(déplacement d'un noeud à un autre " +
                "alors qu'ils ne sont pas considérés comme voisins)%n%n", numberOfNonValidTrips);
        //System.out.format("%d de ces %d trajets non exploitables ont une défaillance provenant (au moins) des %d premiers noeuds ou %d derniers%n", numberOfProblematicNodesAtExtremities, numberOfNonValidTrips, extremitiesNodeNumber, extremitiesNodeNumber);
        System.out.format("Ces trajets non exploitables seront ignorés par la suite, " +
                "%d trajets seront donc traités (initialement %d)%n%n",
                numberOfTrips-numberOfNonValidTrips, numberOfTrips);
    }


    /**
     *
     * Prints the distance and danger values of a specific section
     * @param sectionId The ID of the section
     * @param frequency The number of times the section is present in user data
     * @param graph The Tours graph instance
     * @param userDataSize The size of the user data (= the nomber of trips)
     */
    public void printSectionInformation(long sectionId, long frequency, Graph graph, int userDataSize) {
        Logger log = LoggerFactory.getLogger(Graph.class);

        double distance = graph.getNodeStartBySection(sectionId).getAdjacentNodes().get(graph.getNodeEndBySection(sectionId)).getValue0();
        double danger = graph.getNodeStartBySection(sectionId).getAdjacentNodes().get(graph.getNodeEndBySection(sectionId)).getValue1();

        log.info("ID de la section : {}", sectionId);
        log.info("     longueur  : {}", distance);
        log.info("     danger    : {}", danger);
        log.info("     fréquence : {} pour un total de {} trajets soit {}% de présence", frequency, userDataSize, ((double)frequency)/userDataSize*100);
    }
}
