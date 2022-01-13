package fr.alanguenegou.prd.prdapp.graph;

import lombok.Getter;
import lombok.Setter;
import org.javatuples.*;
import java.util.*;

public class Node {

    @Getter @Setter
    private int id;

    @Getter @Setter
    private List<Node> shortestPath = new LinkedList<>();

    /**
     * distance from source node initialized to infinite positive value
     */
    @Getter @Setter
    private Integer distance = Integer.MAX_VALUE;

    @Getter @Setter
    Map<Node, Pair<Integer, Integer>> adjacentNodes = new HashMap<>();

    /**
     *
     * @param destination noeud voisin
     * @param distance distance avec le noeud voisin
     * @param securite valeur de sécurité sur le tronçon vers le noeud voisin
     */
    public void addNeighbour(Node destination, Integer distance, Integer securite) {
        Pair<Integer, Integer> pair = Pair.with(distance, securite);
        adjacentNodes.put(destination, pair);
    }

    public Node(int id) {
        this.id = id;
    }
}
