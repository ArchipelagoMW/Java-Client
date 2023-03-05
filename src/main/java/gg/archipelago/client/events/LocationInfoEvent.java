package gg.archipelago.client.events;

import gg.archipelago.client.parts.NetworkItem;

import java.util.ArrayList;

public class LocationInfoEvent implements Event{
    public ArrayList<NetworkItem> locations;
    public LocationInfoEvent(ArrayList<NetworkItem> locations) {
        this.locations = locations;
    }
}
