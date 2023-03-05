package gg.archipelago.client.events;

import java.util.ArrayList;

public class CheckedLocationsEvent implements Event {

    public ArrayList<Long> checkedLocations;

    public CheckedLocationsEvent(ArrayList<Long> checkedLocations) {
        this.checkedLocations = checkedLocations;
    }
}
