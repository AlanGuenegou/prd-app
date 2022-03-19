package fr.alanguenegou.prd.prdapp.view;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Dialog {
    /**
     * dialogs with user to retrieve the problem choice
     * @return the problem number asked to be solved
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
     * prints the number of non-valid trips in the user data
     * @param numberOfNonValidTrips number of non-valid trips in user data
     * @param numberOfTrips number of trips in user data
     */
    public void printNumberOfUserDataNonValidTrips (int numberOfNonValidTrips, int numberOfProblematicNodesAtExtremities, int numberOfTrips, int extreminitiesNodeNumber) {
        System.out.format("Dans les données utilisateur, on retrouve un total de %d trajets non exploitables " +
                "car non conformes à la topologie du graphe de Tours%n(déplacement d'un noeud à un autre " +
                "alors qu'ils ne sont pas considérés comme voisins)%n%n", numberOfNonValidTrips);
        System.out.format("%d de ces %d trajets non exploitables ont une défaillance provenant (au moins) des %d premiers noeuds ou %d derniers%n",
                numberOfProblematicNodesAtExtremities, numberOfNonValidTrips, extreminitiesNodeNumber, extreminitiesNodeNumber);
        System.out.format("Ces trajets non exploitables seront ignorés par la suite, " +
                "%d trajets seront donc finalement traités (initialement %d)%n%n",
                numberOfTrips-numberOfNonValidTrips, numberOfTrips);
    }

    public void printTripsSizeStats(int tripsWithEightOrLessNodes, int tripsWithNineToTwentyNodes) {
        System.out.format("%d trajets contiennent un total de 8 noeuds ou moins%n" +
                "%d trajets contiennent entre 9 et 20 noeuds%n%n", tripsWithEightOrLessNodes, tripsWithNineToTwentyNodes);
    }
}
