package fr.alanguenegou.prd.prdapp.graph;

import lombok.Getter;
import lombok.Setter;
import org.javatuples.*;
import java.util.*;

public class Node {

    @Getter @Setter
    private ArrayList<Long> predecessorNodes = new ArrayList<>();

    @Getter @Setter
    private long id;

    @Getter @Setter
    private List<Node> shortestPath = new LinkedList<>();

    /**
     * cost from source node initialized to infinite positive value
     */
    @Getter @Setter
    private Double cost = Double.MAX_VALUE;

    @Getter @Setter
    Map<Node, Pair<Double, Double>> adjacentNodes = new HashMap<>();

    /**
     * adds a neighbour node to this node
     * @param destination neighbour node
     * @param distance distance from this node to the neighbour node
     * @param danger danger value on the section between this node and the neighbour node
     */
    public void addNeighbour(Node destination, Double distance, Double danger) {
        Pair<Double, Double> pair = Pair.with(distance, danger);
        adjacentNodes.put(destination, pair);
    }

    /**
     * constructor of a node with a specific id
     * @param id id of the new node
     */
    public Node(long id) {
        this.id = id;
    }

    /**
     * adds the id of a predecessor node to this node
     * @param nodeId predecessor node id
     */
    public void addPredecessorNode(long nodeId) {
        predecessorNodes.add(nodeId);
    }

    /**
     * resets the cost and path attributes of the node
     */
    public void resetDistanceAndPath() {
        setShortestPath(new LinkedList<>());
        setCost(Double.MAX_VALUE);
    }

    public void modifySectionSecurityValue(long neighbourNodeId, double newSecurityValue) {
        // TODO à écrire


        // passer d'un Pair pour qualifier une section à un Triple avec un "alternate security value", de base initialisé à null
        // si alternate security value != null, alors on prend cette value comme valeur de security à la place de l'autre (penser à multiplier le facteur security par la distance)
        // --> penser à faire la transition Pair -> Triple dans tout le code source
        // utiliser un code binaire 0 1 pour préciser à la méthode de calcul du plus court chemin si on utilise la valeur de base ou celle alternative ??
    }
}
