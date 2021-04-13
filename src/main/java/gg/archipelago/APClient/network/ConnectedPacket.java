package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.APClient.parts.NetworkPlayer;

public class ConnectedPacket extends APPacket {

    @SerializedName("team")
    public int team = -1;
    @SerializedName("slot")
    public int slot = -1;
    @SerializedName("players")
    public NetworkPlayer[] players;
    @SerializedName("missing_locations")
    public int[] missingLocations = new int[]{};
    @SerializedName("checked_locations")
    public int[] checkedLocations = new int[]{};;
}
