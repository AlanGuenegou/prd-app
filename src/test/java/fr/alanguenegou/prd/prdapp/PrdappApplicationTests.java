package fr.alanguenegou.prd.prdapp;

import fr.alanguenegou.prd.prdapp.graph.Graph;
import fr.alanguenegou.prd.prdapp.userdata.UserData;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PrdappApplicationTests {

    @Test
    void contextLoads() {
    }


    // TODO écrire méthode de test pour vérifier le bon calcul du shortest path
    //  --> tester sur un petit graphe fait à la main (20aine de noeuds)
    //  --> utiliser l'exemple sur le tuto suivi sur internet (baeldung)
    @Test
    void checkShortestPathCalculation(Graph graph, UserData userData) {

    }
}
