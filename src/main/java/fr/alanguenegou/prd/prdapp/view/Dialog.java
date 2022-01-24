package fr.alanguenegou.prd.prdapp.view;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Dialog {
    public int problemChoice() {
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
                    System.out.println("Il est nécessaire de rentrer les valeurs 1, 2 ou 3. Veuillez rentrer une valeur à nouveau :");
                    sc.nextLine();
                }
            } catch (InputMismatchException e) {
                System.out.println("Il est nécessaire de rentrer les valeurs 1, 2 ou 3. Veuillez rentrer une valeur à nouveau :");
                sc.nextLine();
            }
        } while (continueInput);
        return choice;
    }
}
