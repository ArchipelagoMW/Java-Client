package io.github.archipelagomw.events;

import io.github.archipelagomw.parts.NetworkItem;

import java.util.ArrayList;

public class LocationInfoEvent implements Event{
    public ArrayList<NetworkItem> locations;
    public LocationInfoEvent(ArrayList<NetworkItem> locations) {
        this.locations = locations;
    }
}
