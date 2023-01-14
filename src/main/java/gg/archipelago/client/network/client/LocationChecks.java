package gg.archipelago.client.network.client;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;

import java.util.HashSet;
import java.util.Set;

public class LocationChecks extends APPacket {

    @SerializedName("locations")
    public Set<Long> locations = new HashSet<>();

    public LocationChecks() {
        this.cmd = APPacketType.LocationChecks;
    }
}
