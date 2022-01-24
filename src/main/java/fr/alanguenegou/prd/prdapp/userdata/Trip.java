package fr.alanguenegou.prd.prdapp.userdata;

import fr.alanguenegou.prd.prdapp.graph.Node;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;

import java.util.LinkedList;
import java.util.List;

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
    // TODO réfléchir à quoi faire de cette étiquette (distance, danger)

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
}
