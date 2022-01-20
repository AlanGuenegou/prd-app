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
    private Double distance = Double.MAX_VALUE;

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
    public Node(int id) {
        this.id = id;
    }
}
