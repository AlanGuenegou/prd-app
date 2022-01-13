package fr.alanguenegou.prd.prdapp.graph;

import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.*;
import java.util.Set;

public class Graph {

    @Getter
    @Setter
    private HashMap<Integer, Node> nodes = new HashMap<>();

    // creates a now node with id as its id and adds it to Graph
    public void addNode(int id){
        Node node = new Node(id);
        nodes.put(id, node);
    }

    // checks if the node with nodeId id is in Graph
    public boolean isNotInGraph(int nodeId){
        return (!nodes.containsKey(nodeId));
    }

    public static Graph calculateShortestPathFromSource(Graph graph, Node source, int distanceWeight) {
        source.setDistance(0);

        // creates a node queue unsettledNodes to explore and keeps track of nodes already explored
        // (= we know their distance from source node)
        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        // adds the source node to the queue
        unsettledNodes.add(source);

        // loop while there are still non explored nodes
        while (unsettledNodes.size() != 0) {

            // chooses the node to explore which is the one with the lowest distance from source node
            Node currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);

            // iterates on every of its neighbours
            for (Entry<Node, Pair<Integer, Integer>> adjacencyPair: currentNode.getAdjacentNodes().entrySet()){
                Node adjacentNode = adjacencyPair.getKey();
                Pair<Integer, Integer> edgeWeights = adjacencyPair.getValue();

                // if this neighbour hasn't been explored yet
                if (!settledNodes.contains(adjacentNode)) {

                    // calculates its minimum distance from the source node
                    calculateMinimumDistance(adjacentNode, edgeWeights, currentNode, distanceWeight);

                    // adds this neighbour to the queue
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return graph;
    }


    private static void calculateMinimumDistance(Node evaluationNode, Pair<Integer, Integer> edgeWeights,
                                                 Node sourceNode, int distanceWeight) {
        Integer sourceDistance = sourceNode.getDistance();

        // linear combination of security value and distance with a distanceWeight weight on distance
        // allows us to apply a Dijkstra algorithm
        int edgeWeight = distanceWeight*edgeWeights.getValue0() + (1-distanceWeight)*edgeWeights.getValue1();


        if (sourceDistance + edgeWeight < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeight);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }


    private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
        Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node node: unsettledNodes){
            int nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    // TODO fonction qui lance le calcul shortest path pour plusieurs CL
    //  et renvoie des résultats sous une forme analysable
}