package fr.alanguenegou.prd.prdapp.userdata;

import fr.alanguenegou.prd.prdapp.graph.Node;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

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


    // TODO vérifier quand est-ce que les problèmes dans les trips arrivent (début ? fin ? milieu / random ?) :
    //  enlever le début et la fin des trips avant analyse ?
    /**
     * checks the validity of every trip by removing the ones that don't "fit" the Tours graph (successor node not in neighbour list)
     * @return the number of trips that are not valid
     */
    public int[] checkTrips() {
        int tripsWithEightOrLessNodes = 0;
        int tripsWitNineToTwentyNodes = 0;

        int numberOfNonValidTrips = 0;

        int numberOfProblematicNodesAtExtremities = 0;

        HashSet<Integer> tripsToRemove = new HashSet<>();

        // iterates through every trip
        for (Trip trip : trips.values()) {

            int tripSize = trip.getTrip().size();
            if (tripSize <= 8) tripsWithEightOrLessNodes++;
            if (tripSize > 8 && tripSize <= 20) tripsWitNineToTwentyNodes++;

            // checks if every node of the trip is in the neighbour list of the previous one
            for (int i = 1; i < tripSize; i++) {

                // if not, keeps the trip in memory
                if(!trip.getTrip().get(i-1).getAdjacentNodes().containsKey(trip.getTrip().get(i))) {
                    tripsToRemove.add(trip.getId());
                    numberOfNonValidTrips++;

                    // TODO résoudre problème trip valides
                    // 3679 sur 38216 trajets non exploitables ont ont une défaillance sur les 2 premiers noeuds ou les 2 derniers
                    // 10000 sur 38k " " 4 premiers et 4 derniers
                    // 15k " " " sur 6 1ers et 6 derniers
                    ArrayList<Integer> tab = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, tripSize - 1, tripSize - 2, tripSize - 3, tripSize - 4, tripSize-5, tripSize-6));

                    if (tab.contains(i)) numberOfProblematicNodesAtExtremities++;


                    break;
                }
            }
        }

        // removes non valid trips
        for (int tripId : tripsToRemove) {
            trips.remove(tripId);
        }



        return new int[] {numberOfNonValidTrips, numberOfProblematicNodesAtExtremities, tripsWithEightOrLessNodes, tripsWitNineToTwentyNodes, 6};
    }
}
