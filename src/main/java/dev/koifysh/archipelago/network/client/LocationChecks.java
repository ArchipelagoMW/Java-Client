package dev.koifysh.archipelago.network.client;

import com.google.gson.annotations.SerializedName;
import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;

import java.util.HashSet;
import java.util.Set;

public class LocationChecks extends APPacket {

    @SerializedName("locations")
    public Set<Long> locations = new HashSet<>();

    public LocationChecks() {
        super(APPacketType.LocationChecks);
    }
}
