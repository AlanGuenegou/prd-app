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

    @Getter @Setter
    private Pair<Double, Double> deducedWeightsValues;

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


    /**
     * gets the distance and danger label of the trip
     * @return the distance and danger label of the trip
     */
    public Pair<Double, Double> getTripValues() {
        double totalDistance = 0;
        double totalDanger = 0;

        // for each node in the trip, gets the values of the section between the node and the next one in the trip
        for (int i = 0; i < trip.size()-1; i++) {

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


    /**
     * compares a real user trip with its calculated version
     * @param graph the graph in which the trip is located
     * @return the percent variation from the closest label in pareto front to the real user trip label
     */
    public Double compareTripWithCalculatedVersion(Graph graph) {
        Pair<Double, Double> tripValues = getTripValues();
        Node startNode = getStartNode();
        Node endNode = getEndNode();

        // HashMap<DistanceWeight, Pair<DistanceValue, SecurityValue>>
        HashMap<Double, Pair<Double, Double>> calculatedLabels = graph.calculateLabelsForManyLinearCombinations(startNode, endNode);

        // computedDifferences is Hashmap<computedDifference, distanceWeight>
        HashMap<Double, Double> computedDifferences = new HashMap<>();

        Set<Double> distanceWeights = calculatedLabels.keySet();

        for (Double distanceWeight : distanceWeights) {
            Double calculatedDistance = calculatedLabels.get(distanceWeight).getValue0();
            Double calculatedSecurity = calculatedLabels.get(distanceWeight).getValue1();

            /*
            Double calculatedTripScore = distanceWeight * calculatedDistance + (1-distanceWeight) * calculatedSecurity;
            Double userTripScore = distanceWeight * tripValues.getValue0() + (1-distanceWeight) * tripValues.getValue1();

            // we assume there will be only one instance of this comparison value within the hashmap computedDifferences ?
            Double comparison = Math.abs(userTripScore / calculatedTripScore - 1);
            */

            // use of euclidean distance
            Double userTripToCalculatedLabelEuclideanDistance = Math.sqrt(
                    Math.pow(calculatedDistance - tripValues.getValue0(), 2)
                    + Math.pow(calculatedSecurity - tripValues.getValue1(), 2)
            );

            computedDifferences.put(userTripToCalculatedLabelEuclideanDistance, distanceWeight);
        }

        // determine the closest label (of pareto front) to real user trip
        Double lengthFromClosestLabel = Collections.min(computedDifferences.keySet());

        /*
        we deduced the distance weight that had been used in this trip, so we can set the deducedWeightsValues attribute of the trip
         */
        Double deducedTripDistanceWeight = computedDifferences.get(lengthFromClosestLabel);
        setDeducedWeightsValues(Pair.with(deducedTripDistanceWeight, 1-deducedTripDistanceWeight));

        /*
        following part is about computing the percent variation from the closest label to the user trip label
        we normalize the coordinate system with the extreme linear combinations labels (1,0) and (0,1).
         */
        double closestLabelDistance = calculatedLabels
                .get(computedDifferences.get(lengthFromClosestLabel))
                .getValue0();

        double closestLabelSecurity = calculatedLabels
                .get(computedDifferences.get(lengthFromClosestLabel))
                .getValue1();

        double tripDistanceWeight = getDeducedWeightsValues().getValue0();
        double tripSecurityWeight = getDeducedWeightsValues().getValue1();

        double userCost = tripDistanceWeight * tripValues.getValue0() / calculatedLabels.get(1.0).getValue0()
                + tripSecurityWeight * tripValues.getValue1() / calculatedLabels.get(0.0).getValue1();

        double closestLabelCost = tripDistanceWeight * closestLabelDistance / calculatedLabels.get(1.0).getValue0()
                + tripSecurityWeight * closestLabelSecurity / calculatedLabels.get(0.0).getValue1();


        return (userCost - closestLabelCost) / closestLabelCost * 100;
    }
}
