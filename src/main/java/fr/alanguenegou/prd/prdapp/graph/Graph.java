package fr.alanguenegou.prd.prdapp.graph;

import lombok.Getter;
import lombok.Setter;
import org.javatuples.*;

import java.util.*;
import java.util.Map.Entry;

/**
 * The class modelling a city road network (here, the city of Tours, France)
 * @author GUENEGOU A.
 * @version 1.00
 */
public class Graph {

    /**
     * Constant giving to the entire app the different distance weights used to compute an artificial Pareto front
     */
    public static final double[] LINEAR_COMBINATION_DISTANCE_WEIGHTS = { 0.001, 0.2, 0.4, 0.5, 0.6, 0.8, 0.999 };


    /**
     * Constant for pointing out what type of danger value will be used by the shortest path algorithm
     */
    public static final int WITH_ALTERNATIVE_DANGER_VALUE = 1;


    /**
     * Constant for pointing out what type of danger value will be used by the shortest path algorithm
     */
    public static final int WITH_INITIAL_DANGER_VALUE = 2;


    /**
     * Convenient mapping for sections : section id -> {node start id, node end id}
     */
    @Getter @Setter
    private HashMap<Long, Pair<Long, Long>> sections = new HashMap<>();


    /**
     * Mapping of nodes modelling this graph
     */
    @Getter @Setter
    private HashMap<Long, Node> nodes = new HashMap<>();


    /**
     * Creates a new node and adds it to this graph
     * @param id The ID of the new node
     */
    public void addNode(long id){
        Node node = new Node(id);
        nodes.put(id, node);
    }


    /**
     * Retrieves a node in this graph by its ID
     * @param id The ID of the node we are looking for
     * @return The node
     */
    public Node getNodeById(long id) {
        return nodes.get(id);
    }


    /**
     * Gets the IDs of start and end nodes of a specific section in this graph
     * @param id The ID of the section
     * @return The pair of nodes start and end of the section
     */
    public Pair<Long, Long> getNodesBySection(long id) {
        return sections.get(id);
    }


    /**
     * Gets the start node of a specific section in this graph
     * @param id The ID of the section
     * @return The starting node of the section
     */
    public Node getNodeStartBySection(long id) {
        return nodes.get(getNodesBySection(id).getValue0());
    }


    /**
     * Gets the end node of a specific section in this graph
     * @param id The ID of the section
     * @return The ending node of the section
     */
    public Node getNodeEndBySection(long id) {
        return nodes.get(getNodesBySection(id).getValue1());
    }


    /**
     * Adds a new section to this {@link Graph#sections} map
     * @param sectionId The ID of the new section
     * @param nodePair The pair of starting and ending nodes IDs of the new section
     */
    public void addSection(long sectionId, Pair<Long, Long> nodePair) {
        sections.put(sectionId, nodePair);
    }


    /**
     * Checks if a specific node is not in {@link Graph#nodes} already
     * @param nodeId The ID of the node
     * @return True if the node with this ID is not in this graph
     */
    public boolean isNotInGraph(long nodeId){
        return !nodes.containsKey(nodeId);
    }


    /**
     * Calculates the shortest-path from a source node to a sink node in this graph
     * (based on the Dijkstra algorithm)
     * @param source The starting node
     * @param sink The ending node
     * @param distanceWeight The attributed distance weight in the linear combination (distance, danger)
     * @param useOfDangerValue A numerical value defining if alternative (= 1) danger value or initial (= 2) one is used for the shortest-path calculation
     * @return The node list that represents the shortest-path from the source to the sink and its total cost
     */
    public static Pair<List<Node>, Double> calculateShortestPathFromSourceToSink(Node source, Node sink, double distanceWeight, int useOfDangerValue) {

        // creates a node queue unsettledNodes to explore and keeps track of nodes already explored
        // (= we know their cost from source node)
        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        // adds the source node to the queue
        source.setCost(0.0);
        unsettledNodes.add(source);

        // loop while there are still non explored nodes
        Node adjacentNode = null;
        outer:
        while (unsettledNodes.size() != 0) {

            // chooses the node to explore which is the one with the lowest cost from source node
            Node currentNode = getLowestCostNode(unsettledNodes);

            unsettledNodes.remove(currentNode);

            // iterates on every of its neighbours
            for (Entry<Node, Triplet<Double, Double, Double>> adjacentNodeEntry : currentNode.getAdjacentNodes().entrySet()) {

                adjacentNode = adjacentNodeEntry.getKey();
                Triplet<Double, Double, Double> edgeValues = adjacentNodeEntry.getValue();

                // if this neighbour hasn't been explored yet
                if (!settledNodes.contains(adjacentNode)) {

                    // calculates its minimum cost from the source node
                    calculateMinimumCost(adjacentNode, edgeValues, currentNode, distanceWeight, useOfDangerValue);

                    // if sink just got explored, breaks the two loops and returns the shortest path
                    if (adjacentNode.equals(sink)) {
                        adjacentNode.getShortestPath().add(adjacentNode);
                        break outer;
                    }

                    // adds this neighbour to the queue
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);

        }
        assert adjacentNode != null;
        return Pair.with(adjacentNode.getShortestPath(), adjacentNode.getCost());
    }


    /**
     * Compares the actual cost of a path with the newly calculated one while following the newly explored path
     * @param evaluationNode The node that has to be evaluated
     * @param edgeValues The distance and danger values of the section between the evaluation node and the source node
     * @param sourceNode The source node of the current evaluation
     * @param distanceWeight The weight of the distance value for the linear combination
     * @param useOfDangerValue A numerical value defining if alternative (= 1) danger value or initial (= 2) one is used for the shortest-path calculation
     */
    private static void calculateMinimumCost(Node evaluationNode, Triplet<Double, Double, Double> edgeValues,
                                             Node sourceNode, double distanceWeight, int useOfDangerValue) {
        Double sourceCost = sourceNode.getCost();

        // linear combination of danger value and distance with a 'distanceWeight' weight on distance
        // allows to apply a classic Dijkstra algorithm
        Double edgeCost;

        // checks if alternative danger value is used for calculation or not
        if (useOfDangerValue == WITH_ALTERNATIVE_DANGER_VALUE && edgeValues.getValue2() != null) {
            edgeCost = distanceWeight*edgeValues.getValue0() + (1-distanceWeight)*edgeValues.getValue2();
        }
        else {
            edgeCost = distanceWeight*edgeValues.getValue0() + (1-distanceWeight)*edgeValues.getValue1();
        }

        // compares the actual cost with the newly calculated one while following the newly explored path
        // if it is better, updates its shortestPath
        if (sourceCost + edgeCost < evaluationNode.getCost()) {
            evaluationNode.setCost(sourceCost + edgeCost);
            LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }


    /**
     * Gets the node with the lowest cost between itself and the source node in a shortest-path algorithm
     * @param unsettledNodes The list of nodes that remain unexplored
     * @return The node in the list having the lowest cost
     */
    private static Node getLowestCostNode(Set<Node> unsettledNodes) {
        Node nodeWithLowestCost = null;
        Double lowestCost = Double.MAX_VALUE;
        for (Node node: unsettledNodes) {
            Double nodeCost = node.getCost();
            if (nodeCost < lowestCost) {
                lowestCost = nodeCost;
                nodeWithLowestCost = node;
            }
        }
        return nodeWithLowestCost;
    }


    /**
     * Computes the labels (distance, danger) of a shortest-path, for numerous linear combination weights
     * @param source The starting node of the path request
     * @param sink The ending node of the path request
     * @param useOfDangerValue A numerical value defining if alternative (= 1) danger value or initial (= 2) one is used for the shortest-path calculation
     * @return A HashMap containing the distance and danger values for X linear combinations (see {@link Graph#LINEAR_COMBINATION_DISTANCE_WEIGHTS})
     */
    public HashMap<Double, Pair<Double, Double>> calculateLabelsForManyLinearCombinations(Node source, Node sink, int useOfDangerValue) {
        HashMap<Double, Pair<Double, Double>> labels = new HashMap<>(LINEAR_COMBINATION_DISTANCE_WEIGHTS.length);
        for (double distanceWeight: LINEAR_COMBINATION_DISTANCE_WEIGHTS) {
            prepareNewCalculation();
            Pair<List<Node>, Double> shortestPath = calculateShortestPathFromSourceToSink(source, sink, distanceWeight, useOfDangerValue);

            double totalDistance = 0;
            double totalDanger = 0;

            // for each node in the computed trip, gets the values of the section between the node and the next one
            for (int i = 0; i < shortestPath.getValue0().size()-1; i++) {
                totalDistance += shortestPath.getValue0().get(i).getAdjacentNodes().get(shortestPath.getValue0().get(i+1)).getValue0();

                if (useOfDangerValue == WITH_ALTERNATIVE_DANGER_VALUE &&
                        shortestPath.getValue0().get(i).getAdjacentNodes().get(shortestPath.getValue0().get(i+1)).getValue2() != null) {
                    totalDanger += shortestPath.getValue0().get(i).getAdjacentNodes().get(shortestPath.getValue0().get(i+1)).getValue2();
                }
                else {
                    totalDanger += shortestPath.getValue0().get(i).getAdjacentNodes().get(shortestPath.getValue0().get(i+1)).getValue1();
                }
            }

            labels.put(distanceWeight, Pair.with(totalDistance, totalDanger));
        }
        return labels;
    }


    /**
     * Checks if, for a specific path request, its labels are close enough to each other,
     * or if an additional linear combination between two is needed
     * @param labels The pareto front of a computed shortest-path
     */
    public void checkLinearCombinationAmount(HashMap<Double, Pair<Double, Double>> labels) {

        // hashmap of label distance weight and its distance with the next label
        HashMap<Double, Double> distancesBetweenLinearCombinations = new HashMap<>();

        // list of all distance weights
        LinkedList<Double> distanceWeights = new LinkedList<>(labels.keySet());

        // computes distance between each label
        for (Entry<Double, Pair<Double, Double>> label : labels.entrySet()) {
            if (!distanceWeights.getLast().equals(label.getKey())) {
                // finds following label key
                double nextDistanceWeight = distanceWeights.get(distanceWeights.indexOf(label.getKey()) + 1);

                // next label values distance, danger
                Pair<Double, Double> nextLabel = Pair.with(labels.get(nextDistanceWeight).getValue0(), labels.get(nextDistanceWeight).getValue1());


                double distance = Math.sqrt(Math.pow(label.getValue().getValue0() - nextLabel.getValue0(), 2) +
                        Math.pow(label.getValue().getValue1() - nextLabel.getValue1(), 2));


                // distance = distance between the label with this distance weight and the next one
                distancesBetweenLinearCombinations.put(label.getKey(), distance);
            }
        }

        double longestDistance = Collections.max(distancesBetweenLinearCombinations.values());
        double longestDistanceDistanceWeight = 0;
        for (double distanceWeight : distancesBetweenLinearCombinations.keySet()) {
            if (distancesBetweenLinearCombinations.get(distanceWeight) == longestDistance)
                longestDistanceDistanceWeight = distanceWeight;
        }

        // computes distance between extreme labels linear combinations
        Pair<Double, Double> extremeDistanceLabel = labels.get(LINEAR_COMBINATION_DISTANCE_WEIGHTS[6]);
        Pair<Double, Double> extremeDangerLabel = labels.get(LINEAR_COMBINATION_DISTANCE_WEIGHTS[0]);


        double distanceBetweenBothExtremes = Math.sqrt(Math.pow(extremeDistanceLabel.getValue0() - extremeDangerLabel.getValue0(), 2) +
                Math.pow(extremeDistanceLabel.getValue1() - extremeDangerLabel.getValue1(), 2));

        double rapport = longestDistance / (distanceBetweenBothExtremes / (labels.size()-1));

        if (rapport >= 3) {
            double nextDistanceWeight = distanceWeights.get(distanceWeights.indexOf(longestDistanceDistanceWeight) + 1);

            System.out.format("L'écart entre les labels ayant un poids de distance %f et %f semble trop élevé (le rapport calculé est de %f)%n",
                    longestDistanceDistanceWeight, nextDistanceWeight, rapport);
        }
        else {
            System.out.format("Les écarts entre les CL semblent être raisonnables%n");
        }
    }


     /**
      * Prints the number of nodes in this graph that only have one predecessor and one successor
      */
    public void printNumberOfNodesHavingOnePredecessorAndSuccessor() {
        long numberOfNodes = 0;
        int numberOfDeadEnd = 0;
        for (Node node : nodes.values()) {
            if (node.getPredecessorNodes().size() == 1 && node.getAdjacentNodes().size() == 1) {
                numberOfNodes++;
                if (node.getPredecessorNodes().contains(((Node)node.getAdjacentNodes().keySet().toArray()[0]).getId())) {
                    numberOfDeadEnd++;
                }
            }
        }
        System.out.println("-----------------------------------------------------------------");
        System.out.println();
        System.out.println("Analyse du nombre de noeuds \"inutiles\" dans l'objet graphe rempli :");
        System.out.print("Il y a " + numberOfNodes + " noeuds possédant un seul prédécesseur et un seul successeur, " +
                "sur un total de " + nodes.size() + " noeuds dans le graphe de Tours.");
        System.out.format(" Soit %.2f%% des noeuds%n", numberOfNodes/(double)nodes.size()*100);
        System.out.format("Parmi ces noeuds, %d sont des impasses où le prédécesseur et le successeur sont le même noeud%n", numberOfDeadEnd);
        System.out.println();
        System.out.println("-----------------------------------------------------------------");
    }


    /**
     * Prepares new calculation of a path by resetting distance and path attributes of every node of this graph
     */
    public void prepareNewCalculation() {
        for (Node node: getNodes().values()) {
            node.resetDistanceAndPath();
        }
    }


    /**
     * Applies a set of modifications to the Danger value of some specific nodes of this graph
     * @param modifications A HashMap that contains each section on which we want to apply a modification
     *                      ({section ID, new security factor})
     */
    public void modifyGraph(HashMap<Long, Integer> modifications) {
        // for each section that has to be modified
        for (Entry<Long, Integer> modification : modifications.entrySet()) {

            // modifies its danger value
            getNodeStartBySection(modification.getKey()).modifySectionDangerValue(getNodeEndBySection(modification.getKey()), modification.getValue());
        }
    }

}