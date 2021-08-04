package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.APClient.parts.NetworkPlayer;

import java.util.ArrayList;
import java.util.HashSet;

public class ConnectedPacket extends APPacket {

    @SerializedName("team")
    public int team = -1;
    @SerializedName("slot")
    public int slot = -1;
    @SerializedName("players")
    public ArrayList<NetworkPlayer> players;
    @SerializedName("missing_locations")
    public HashSet<Integer> missingLocations = new HashSet<>();
    @SerializedName("checked_locations")
    public HashSet<Integer> checkedLocations = new HashSet<>();
}
