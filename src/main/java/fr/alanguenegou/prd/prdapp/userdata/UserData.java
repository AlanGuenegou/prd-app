package fr.alanguenegou.prd.prdapp.userdata;

import fr.alanguenegou.prd.prdapp.graph.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

public class UserData {

    @Getter @Setter
    private HashMap<Integer, Trip> trips = new HashMap<>();

    /**
     * adds a new trip to trips
     * @param tripId id of the trip
     * @param nodeList list of nodes
     */
    public void addTrip(int tripId, List<Node> nodeList) {
        Trip trip = new Trip(tripId, nodeList);
        trips.put(tripId, trip);
    }

    /**
     * adds a node to a specific trip contained in trips
     * @param tripId id of the trip
     * @param node node that has to be added to the trip
     */
    public void addNodeToTrip(int tripId, Node node) {
        trips.get(tripId).addNode(node);
    }

    /**
     * checks if the node is not in the trip
     * @param tripId id of the trip
     * @param node node that has to be checked
     * @return boolean of the non presence of the node in the trip
     */
    public boolean isNotInTrip(int tripId, Node node) {
        return !trips.get(tripId).getTrip().contains(node);
    }

    // TODO fonction qui compare un trip avec la version calculée par la méthode de la classe graph
    // TODO vérifier comment comparer le front de pareto artificiel avec le label du trip
}
