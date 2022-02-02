package fr.alanguenegou.prd.prdapp.userdata;

import fr.alanguenegou.prd.prdapp.graph.Graph;
import fr.alanguenegou.prd.prdapp.graph.Node;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;

import java.util.*;

public class Trip {

    @Getter @Setter
    private int id;

    @Getter @Setter
    private List<Node> trip = new LinkedList<>();

    /**
     * constructor of a trip
     * @param id id of the new trip
     * @param trip list of nodes that constitute the trip
     */
    public Trip(int id, List<Node> trip) {
        this.setId(id);
        this.setTrip(trip);
    }

    /**
     * adds a specific node to the trip
     * @param node node that is added
     */
    public void addNode(Node node){
        trip.add(node);
    }

    // TODO vérifier que la méthode getTripValues fonctionne

    /**
     * gets the distance and danger label of the trip
     * @return the distance and danger label of the trip
     */
    public Pair<Double, Double> getTripValues() {
        double totalDistance = 0;
        double totalDanger = 0;

        // for each node in the trip, gets the values of the section between the node and the next one in the trip
        int tripSize = trip.size();
        for (int i = 0; i < tripSize-1; i++) {
            totalDistance += trip.get(i).getAdjacentNodes().get(trip.get(i+1)).getValue0();
            totalDanger += trip.get(i).getAdjacentNodes().get(trip.get(i+1)).getValue1();
        }

        return Pair.with(totalDistance, totalDanger);
    }

    /**
     * gets the starting node of the trip
     * @return starting node of the trip
     */
    public Node getStartNode() {
        return trip.get(0);
    }

    /**
     * gets the ending node of the trip
     * @return the ending node of the trip
     */
    public Node getEndNode() {
        return trip.get(trip.size()-1);
    }


    // TODO vérifier que la méthode fonctionne et que la stratégie de calcul est bonne
    /**
     * compares a real user trip with its calculated version
     * @param graph the graph in which the trip is located
     * @return the difference (in percentage/100) between a real user trip and its calculated version
     */
    public Double compareTripWithCalculatedVersion(Graph graph) {
        Pair<Double, Double> tripValues = getTripValues();
        Node startNode = getStartNode();
        Node endNode = getEndNode();

        HashMap<Double, Pair<Double, Double>> calculatedLabels = graph.calculateLabelsForManyLinearCombinations(startNode, endNode);

        List<Double> computedDifferences = new ArrayList<>();

        Set<Double> keySet = calculatedLabels.keySet();
        for (Double key : keySet) {
            Double calculatedDistance = calculatedLabels.get(key).getValue0();
            Double calculatedDanger = calculatedLabels.get(key).getValue1();

            Double computedDistanceDifference = Math.abs(calculatedDistance / tripValues.getValue0() - 1);
            Double computedDangerDifference = Math.abs(calculatedDanger / tripValues.getValue1() - 1);


            // computes difference (percentage/100) between calculated label and real trip values
            computedDifferences.add((computedDangerDifference+computedDistanceDifference)/2);
        }

        return Collections.min(computedDifferences);
    }
}
