package dev.koifysh.archipelago.network.server;

import com.google.gson.annotations.SerializedName;
import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;
import dev.koifysh.archipelago.parts.NetworkItem;

import java.util.ArrayList;

public class LocationInfoPacket extends APPacket {

    @SerializedName("locations")
    public ArrayList<NetworkItem> locations;

    public LocationInfoPacket() {
        super(APPacketType.LocationInfo);
    }
}
