package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.APClient.parts.NetworkItem;

import java.util.ArrayList;

public class LocationInfo extends APPacket {

    @SerializedName("locations")
    public ArrayList<NetworkItem> locations;

}
