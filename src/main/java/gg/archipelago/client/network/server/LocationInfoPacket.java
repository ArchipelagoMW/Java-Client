package gg.archipelago.client.network.server;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;
import gg.archipelago.client.parts.NetworkItem;

import java.util.ArrayList;

public class LocationInfoPacket extends APPacket {

    @SerializedName("locations")
    public ArrayList<NetworkItem> locations;

    public LocationInfoPacket() {
        super(APPacketType.LocationInfo);
    }
}
