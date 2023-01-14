package gg.archipelago.client.network.client;

import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;

import java.util.ArrayList;

public class LocationScouts extends APPacket {

    public ArrayList<Long> locations = new ArrayList<>();

    public LocationScouts(ArrayList<Long> locations) {
        this.cmd = APPacketType.LocationScouts;
        this.locations = locations;
    }
}
