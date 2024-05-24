package dev.koifysh.archipelago.network.client;

import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;

import java.util.ArrayList;

public class LocationScouts extends APPacket {

    public ArrayList<Long> locations = new ArrayList<>();

    public LocationScouts(ArrayList<Long> locations) {
        super(APPacketType.LocationScouts);
        this.locations = locations;
    }
}
