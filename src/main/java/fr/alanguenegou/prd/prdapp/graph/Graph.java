package fr.alanguenegou.prd.prdapp.graph;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import fr.alanguenegou.prd.prdapp.dbaccess.GraphDataAccess;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

public class Graph {
    private final static Logger log = LoggerFactory.getLogger(Graph.class);

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
     * adds a new section to the 'sections' map
     * @param sectionId id of the new section
     * @param nodePair pair of starting and ending nodes of the new section
     */
    public void addSection(long sectionId, Pair<Long, Long> nodePair) {
        sections.put(sectionId, nodePair);
    }


    // TODO méthode finalement abandonnée : à supprimer
    /**
     * checks if a pair of nodes is already in 'sections' values
     * @param nodeStartId id of starting node of section
     * @param nodeEndId id of ending node of section
     * @return boolean of if the pair of nodes is already in 'sections'
     */
    public boolean isRoutelinkValueAlreadyInSections(long nodeStartId, long nodeEndId) {
        for (Pair<Long, Long> sectionValues : sections.values()) {
            if (sectionValues.equals(Pair.with(nodeStartId, nodeEndId))) {
                return true;
            }
        }
        return false;
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
     * @return the node list that represents the shortest path from source to sink and its total cost
     */
    public static Pair<List<Node>, Double> calculateShortestPathFromSourceToSink(Node source, Node sink, double distanceWeight) {

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
            for (Entry<Node, Pair<Double, Double>> adjacencyPair : currentNode.getAdjacentNodes().entrySet()) {

                adjacentNode = adjacencyPair.getKey();
                Pair<Double, Double> edgeValues = adjacencyPair.getValue();

                // if this neighbour hasn't been explored yet
                if (!settledNodes.contains(adjacentNode)) {

                    // calculates its minimum cost from the source node
                    calculateMinimumCost(adjacentNode, edgeValues, currentNode, distanceWeight);

                    // if sink just got explored, breaks the two loops and returns the shortest path
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
        return Pair.with(adjacentNode.getShortestPath(), adjacentNode.getCost());
    }

    /**
     * compares the actual cost with the newly calculated one while following the newly explored path
     * @param evaluationNode node that has to be evaluated
     * @param edgeValues distance and danger values of the section between evaluationNode and sourceNode
     * @param sourceNode source node of the current evaluation
     * @param distanceWeight weight of the distance value for the linear combination
     */
    private static void calculateMinimumCost(Node evaluationNode, Pair<Double, Double> edgeValues,
                                             Node sourceNode, double distanceWeight) {
        Double sourceCost = sourceNode.getCost();

        // linear combination of security value and distance with a distanceWeight weight on distance
        // allows us to apply a Dijkstra algorithm
        Double edgeCost = distanceWeight*edgeValues.getValue0() + (1-distanceWeight)*edgeValues.getValue1();


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
     * gets the node with the lowest cost between itself and the source node
     * @param unsettledNodes list of nodes that remain unexplored
     * @return the node in the list with the lowest cost
     */
    private static Node getLowestCostNode(Set<Node> unsettledNodes) {
        Node lowestCostNode = null;
        Double lowestCost = Double.MAX_VALUE;
        for (Node node: unsettledNodes) {
            Double nodeCost = node.getCost();
            if (nodeCost < lowestCost) {
                lowestCost = nodeCost;
                lowestCostNode = node;
            }
        }
        return lowestCostNode;
    } // OK


    /**
     * computes the labels (distance, danger) of a shortest-path, for multiple linear combination weights
     * @param source starting node of the path request
     * @param sink ending node of the path request
     * @return hashmap containing the distance and security values for 7 linear combinations
     */
    public HashMap<Double, Pair<Double, Double>> calculateLabelsForManyLinearCombinations(Node source, Node sink) {
        double[] distanceWeights = {0.0, 0.2, 0.4, 0.5, 0.6, 0.8, 1.0};
        HashMap<Double, Pair<Double, Double>> labels = new HashMap<>(distanceWeights.length);
        for (double distanceWeight: distanceWeights) {
            double cost = calculateShortestPathFromSourceToSink(source, sink, distanceWeight).getValue1();
            labels.put(distanceWeight, Pair.with(distanceWeight*cost, (1-distanceWeight)*cost));
        }
        return labels;
    }


    // TODO quoi faire avec la méthode checkLinearCombinationAmount qui vérifié les écarts pour un seul trajet ???
    //  = rajouter des CL dans la construction du front de pareto (20 CL max !)
    /**
     * checks if, for a specific path request, its labels are enough close to each other,
     * or if an additional linear combination between two is needed
     * @param labels the pareto front of a computed shortest-path of a path request
     */
    private void checkLinearCombinationAmount(HashMap<Double, Pair<Double, Double>> labels) {

        // hashmap of label distance weight and its distance with the next label
        HashMap<Double, Double> distancesBetweenLinearCombinations = new HashMap<>();

        // list of all distance weights
        LinkedList<Double> distanceWeights = new LinkedList<>(distancesBetweenLinearCombinations.keySet());

        // computes distance between each label
        for (Double key : labels.keySet()) {
            // finds following label key
            double nextDistanceWeight = distanceWeights.get(distanceWeights.indexOf(key) + 1);

            // label values distance, security
            Pair<Double, Double> actualLabel = Pair.with(labels.get(key).getValue0(), labels.get(key).getValue1());
            Pair<Double, Double> nextLabel = Pair.with(labels.get(nextDistanceWeight).getValue0(), labels.get(nextDistanceWeight).getValue1());


            double distance = Math.sqrt(Math.pow(actualLabel.getValue0() - nextLabel.getValue0(), 2) +
                    Math.pow(actualLabel.getValue1() - nextLabel.getValue1(), 2));


            // key = distance weight
            // distance = distance between the label with this distance weight and the next one
            distancesBetweenLinearCombinations.put(key, distance);

        }

        double longestDistance = Collections.max(distancesBetweenLinearCombinations.values());
        double longestDistanceDistanceWeight = 0;
        for (double distanceWeight : distancesBetweenLinearCombinations.keySet()) {
            if (distancesBetweenLinearCombinations.get(distanceWeight) == longestDistance)
                longestDistanceDistanceWeight = distanceWeight;
        }

        // computes distance between extreme labels (1,0) and (0,1) linear combinations
        Pair<Double, Double> extremeDistanceLabel = Pair.with(labels.get(1.0).getValue0(), labels.get(1.0).getValue1());
        Pair<Double, Double> extremeSecurityLabel = Pair.with(labels.get(0.0).getValue0(), labels.get(0.0).getValue1());


        double distanceBetweenBothExtremes = Math.sqrt(Math.pow(extremeDistanceLabel.getValue0() - extremeSecurityLabel.getValue0(), 2) +
                Math.pow(extremeDistanceLabel.getValue1() - extremeSecurityLabel.getValue1(), 2));

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
      * prints the number of nodes in the graph that only have one predecessor and one successor
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


    // TODO méthode abandonnée (voir notes words) : à supprimer
    /**
     * removes nodes in Graph that only have one predecessor and one successor (= useless nodes)
     * and then link predecessors and successors together each time it is needed
     */
    public void removeUselessNodes() {
        log.info("Lancement de la méthode removeUselessNodes()...");

        // converts sections HashMap to a BiMap to later search for specific sections
        HashSet<Long> nodesToRemove = new HashSet<>();

        BiMap<Long, Pair<Long, Long>> bimapSections = HashBiMap.create(sections);

        // iterates over every node of the Tours graph
        for (Node node : nodes.values()) {


            // if node is useless
            if (node.getPredecessorNodes().size() == 1 && node.getAdjacentNodes().size() == 1) {

                /*
                section dedicated to : delete node in 'sections' attribute
                 */
                Node successorNode = (Node) node.getAdjacentNodes().keySet().toArray()[0];
                Node predecessorNode = nodes.get(node.getPredecessorNodes().get(0));

                // if this is a "cul de sac" situation, pass
                if (successorNode.equals(predecessorNode)) continue;


                // changes sections concerned by this specific node
                long firstSectionId = bimapSections.inverse().get(Pair.with(node.getId(), successorNode.getId()));
                long secondSectionId = bimapSections.inverse().get(Pair.with(predecessorNode.getId(), node.getId()));

                Pair<Long, Long> newValidNodesPair = Pair.with(predecessorNode.getId(), successorNode.getId());
                sections.replace(firstSectionId, newValidNodesPair);
                sections.replace(secondSectionId, newValidNodesPair);


                /*
                section dedicated to : delete node in 'nodes' attribute that represents the Tours graph
                 */
                // put successor to predecessor's neighbors list
                Pair<Double, Double> newCost = Pair.with(
                        predecessorNode.getAdjacentNodes().get(node).getValue0() + node.getAdjacentNodes().get(successorNode).getValue0(),
                        predecessorNode.getAdjacentNodes().get(node).getValue1() + node.getAdjacentNodes().get(successorNode).getValue1()
                );
                predecessorNode.getAdjacentNodes().put(successorNode, newCost);

                // adds predecessor to successor's predecessors list
                successorNode.getPredecessorNodes().add(predecessorNode.getId());

                // removes node from successor's predecessors list and from predecessor's neighbors list
                successorNode.getPredecessorNodes().remove(node.getId());
                predecessorNode.getAdjacentNodes().remove(node);

                // keeps in memory this useless node's id
                nodesToRemove.add(node.getId());
            }
        }

        // remove all useless nodes
        for (long nodeId: nodesToRemove) {
            nodes.remove(nodeId);
        }

        log.info("Fin de la méthode removeUselessNodes() !");
    }

    /**
     * prepares new calculation of a path by resetting distance and path attributes of every node of the graph
     */
    public void prepareNewCalculation() {
        for (Node node: getNodes().values()) {
            node.resetDistanceAndPath();
        }
    }

    /**
     *
     * @param modifications contains each section on which we want to apply a modification
     *                      (HashMap<Pair<starting node id, ending node id>, modified security value>)
     */
    public void modifyGraph(HashMap<Pair<Long, Long>, Long> modifications) {
        // for each section that has to be modified
        for (Pair<Long, Long> section : modifications.keySet()) {

            // modifies its security value
            nodes.get(section.getValue0()).modifySectionSecurityValue(section.getValue1(), modifications.get(section));
        }
    }
}