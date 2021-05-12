package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.APClient.parts.NetworkPlayer;

import java.util.ArrayList;

public class ConnectedPacket extends APPacket {

    @SerializedName("team")
    public int team = -1;
    @SerializedName("slot")
    public int slot = -1;
    @SerializedName("players")
    public ArrayList<NetworkPlayer> players;
    @SerializedName("missing_locations")
    public int[] missingLocations = new int[]{};
    @SerializedName("checked_locations")
    public int[] checkedLocations = new int[]{};;
}
