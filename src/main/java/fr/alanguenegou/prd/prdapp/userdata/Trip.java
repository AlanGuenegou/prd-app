package fr.alanguenegou.prd.prdapp.userdata;

import fr.alanguenegou.prd.prdapp.graph.Node;
import lombok.Getter;
import lombok.Setter;

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

    // TODO écrire méthode qui calcule la distance d'un trip
}
