package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocationChecks extends APPacket {

    @SerializedName("locations")
    public Set<Long> locations = new HashSet<>();

    public LocationChecks() {
        this.cmd = APPacketType.LocationChecks;
    }
}
