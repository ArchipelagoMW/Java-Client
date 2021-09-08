package gg.archipelago.APClient.network;

import java.sql.Array;
import java.util.ArrayList;

public class LocationScouts extends APPacket {

    public ArrayList<Integer> locations = new ArrayList<>();

    public LocationScouts(ArrayList<Integer> locations) {
        this.cmd = APPacketType.LocationScouts;
        this.locations = locations;
    }
}
