package io.github.archipelagomw.network.server;

import com.google.gson.annotations.SerializedName;
import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;
import io.github.archipelagomw.parts.NetworkItem;

import java.util.ArrayList;

public class LocationInfoPacket extends APPacket {

    @SerializedName("locations")
    public ArrayList<NetworkItem> locations;

    public LocationInfoPacket() {
        super(APPacketType.LocationInfo);
    }
}
