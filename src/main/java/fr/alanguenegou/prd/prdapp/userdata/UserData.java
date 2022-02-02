package fr.alanguenegou.prd.prdapp.userdata;

import fr.alanguenegou.prd.prdapp.graph.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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

    // TODO fonction qui compare tous les trips avec leur version calculée par la méthode de la classe graph,
    //  et qui retourne la différence moyenne, minimum, maximum etc...
    // TODO nécessité de couper le début et la fin du trip pour la comparaison ?? par rapport à ce que disait Mr Sauvanet


    // TODO méthode pour checker la validité des trips (supprimer les trips qui sont pas valides)

    /**
     * checks the validity of every trip by removing the ones that don't "fit" the Tours graph (successor node not in neighbour list)
     * @return the number of trips that are not valid
     */
    public int checkTrips() {
        int numberOfNonValidTrips = 0;
        List<Integer> tripsToRemove = new ArrayList<>();

        // iterates through every trip
        for (Trip trip : trips.values()) {

            int tripSize = trip.getTrip().size();

            // checks if every node of the trip is in the neighbour list of the previous one
            for (int i = 0; i < tripSize-1; i++) {

                // if not, keeps the trip in memory
                if(!trip.getTrip().get(i).getAdjacentNodes().containsKey(trip.getTrip().get(i+1))) {
                    tripsToRemove.add(trip.getId());
                    numberOfNonValidTrips++;
                    break;
                }
            }
        }

        // removes non valid trips
        for (int tripId : tripsToRemove) {
            trips.remove(tripId);
        }

        return numberOfNonValidTrips;
    }
}
