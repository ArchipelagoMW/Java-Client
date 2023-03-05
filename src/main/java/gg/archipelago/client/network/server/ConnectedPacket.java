package gg.archipelago.client.network.server;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;
import gg.archipelago.client.parts.NetworkPlayer;

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
    public HashSet<Long> missingLocations = new HashSet<>();
    @SerializedName("checked_locations")
    public HashSet<Long> checkedLocations = new HashSet<>();

    public ConnectedPacket() {
        super(APPacketType.Connected);
    }
}
