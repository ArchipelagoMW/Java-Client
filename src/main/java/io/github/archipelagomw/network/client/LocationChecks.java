package io.github.archipelagomw.network.client;

import com.google.gson.annotations.SerializedName;
import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;

import java.util.HashSet;
import java.util.Set;

public class LocationChecks extends APPacket {

    @SerializedName("locations")
    public Set<Long> locations = new HashSet<>();

    public LocationChecks() {
        super(APPacketType.LocationChecks);
    }
}
