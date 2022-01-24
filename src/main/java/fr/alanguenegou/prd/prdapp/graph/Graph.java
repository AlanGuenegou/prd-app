package fr.alanguenegou.prd.prdapp.graph;

import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;

import java.util.*;
import java.util.Map.Entry;

public class Graph {
    // TODO vérifier les noms "cost" et "distance" et remettre ça au propre (prioriser "cost" pour quand c'est une CL)

    @Getter @Setter
    /*
    convenient mapping for sections : section id -> <node start id, node end id>
     */
    private HashMap<Long, Pair<Long, Long>> sections = new HashMap<>();

    @Getter @Setter
    private HashMap<Long, Node> nodes = new HashMap<>();

    /**
     * creates a new node and adds it to the graph
     * @param id id of the new node
     */
    public void addNode(long id){
        Node node = new Node(id);
        nodes.put(id, node);
    }

    /**
     * get the ids of start and end nodes of a specific section
     * @param id id of the section
     * @return pair of nodes start and end of section
     */
    public Pair<Long, Long> getNodesBySection(long id) {
        return sections.get(id);
    }

    /**
     * gets the start node of a specific section
     * @param id id of the section
     * @return starting node id of the section
     */
    public Node getNodeStartBySection(long id) {
        return nodes.get(getNodesBySection(id).getValue0());
    }

    /**
     * gets the end node of a specific section
     * @param id id of the section
     * @return ending node id of the section
     */
    public Node getNodeEndBySection(long id) {
        return nodes.get(getNodesBySection(id).getValue1());
    }

    /**
     * adds a new section to the section map
     * @param nodeStartId id of the node starting the section
     * @param nodeEndId id of the node ending the section
     * @param sectionId id of the section
     */
    public void addSection(long nodeStartId, long nodeEndId, long sectionId) {
        sections.put(sectionId, Pair.with(nodeStartId, nodeEndId));
    }


    /**
     * checks if the node with nodeId as id is not in the graph
     * @param nodeId id of the node
     * @return the node with this id is not in the graph
     */
    public boolean isNotInGraph(long nodeId){
        return !nodes.containsKey(nodeId);
    }

    // TODO vérifier que la méthode fonctionne
    /**
     * calculates the shortest path from a source node to a sink node
     * @param source starting node
     * @param sink ending node
     * @param distanceWeight attributed distance weight in the linear combination (distance, danger)
     * @return the node list that represents the shortest path from source to sink and its distance
     */
    public static Pair<List<Node>, Double> calculateShortestPathFromSourceToSink(Node source, Node sink, double distanceWeight) {
        source.setDistance((double) 0);

        // creates a node queue unsettledNodes to explore and keeps track of nodes already explored
        // (= we know their distance from source node)
        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        // adds the source node to the queue
        unsettledNodes.add(source);

        // loop while there are still non explored nodes
        Node adjacentNode = null;
        outer:
        while (unsettledNodes.size() != 0) {

            // chooses the node to explore which is the one with the lowest distance from source node
            Node currentNode = getLowestDistanceNode(unsettledNodes);

            unsettledNodes.remove(currentNode);

            // iterates on every of its neighbours
            for (Entry<Node, Pair<Double, Double>> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {

                adjacentNode = adjacencyPair.getKey();
                Pair<Double, Double> edgeValues = adjacencyPair.getValue();

                // if this neighbour hasn't been explored yet
                if (!settledNodes.contains(adjacentNode)) {

                    // calculates its minimum distance from the source node
                    calculateMinimumDistance(adjacentNode, edgeValues, currentNode, distanceWeight);

                    // if sink just go explored, breaks the two loops and returns the shortest path
                    if (adjacentNode.equals(sink)) {
                        break outer;
                    }

                    // adds this neighbour to the queue
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);

        }
        assert adjacentNode != null;
        return Pair.with(adjacentNode.getShortestPath(), adjacentNode.getDistance());
    }

    /**
     * compares the actual distance with the newly calculated one while following the newly explored path
     * @param evaluationNode node that has to be evaluated
     * @param edgeValues distance and danger values of the section between evaluationNode and sourceNode
     * @param sourceNode source node of the current evaluation
     * @param distanceWeight weight of the distance value for the linear combination
     */
    private static void calculateMinimumDistance(Node evaluationNode, Pair<Double, Double> edgeValues,
                                                 Node sourceNode, double distanceWeight) {
        Double sourceDistance = sourceNode.getDistance();

        // linear combination of security value and distance with a distanceWeight weight on distance
        // allows us to apply a Dijkstra algorithm
        Double edgeWeight = distanceWeight*edgeValues.getValue0() + (1-distanceWeight)*edgeValues.getValue1();


        // compares the actual distance with the newly calculated one while following the newly explored path
        // if it is better, updates its shortestPath
        if (sourceDistance + edgeWeight < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeight);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }


    /**
     * gets the node with the lowest distance between it and the source node
     * @param unsettledNodes list of nodes that remain unexplored
     * @return the node in the list with the lowest distance
     */
    private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
        Node lowestDistanceNode = null;
        Double lowestDistance = Double.MAX_VALUE;
        for (Node node: unsettledNodes){
            Double nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    /**
     * computes the labels (distance, danger) of a shortest-path, for multiple linear combination weights
     * @param source starting node of the path request
     * @param sink ending node of the path request
     * @return hashmap containing the distance and danger values for 7 linear combinations
     */
    public HashMap<Double, Pair<Double, Double>> calculateLabelsForManyLinearCombinations(Node source, Node sink) {
        double[] distanceWeights = {0, 0.2, 0.4, 0.5, 0.6, 0.8, 1};
        HashMap<Double, Pair<Double, Double>> labels = new HashMap<>(7);
        for (double distanceWeight: distanceWeights) {
            double cost = calculateShortestPathFromSourceToSink(source, sink, distanceWeight).getValue1();
            labels.put(distanceWeight, Pair.with(distanceWeight*cost, (1-distanceWeight)*cost));
        }
        return labels;
    }

    // TODO dev un moyen de supprimer les noeuds inutiles :
    //  écrire une méthode qui relie les données utilisateur et le graph, et supprimer les noeuds dans le graph et les trips
    /**
      * prints the number of nodes in the graph that only have one predecessor and one successor
      */
    public void printNumberOfNodesHavingOnePredecessorAndSuccessor() {
        long numberOfNodes = 0;
        for (Node node : nodes.values()) {
            if (node.getPredecessorNumber() == 1 && node.getAdjacentNodes().size() == 1) numberOfNodes++;
        }
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Analyse du nombre de noeuds \"inutiles\" dans l'objet graphe rempli :");
        System.out.print("Il y a " + numberOfNodes + " noeuds possédant un seul prédécesseur et un seul successeur, " +
                "sur un total de " + nodes.size() + " noeuds dans le graphe de Tours.");
        System.out.format(" Soit %.2f%% des noeuds\n", numberOfNodes/(double)nodes.size()*100);
        System.out.println("-----------------------------------------------------------------");
    }
}