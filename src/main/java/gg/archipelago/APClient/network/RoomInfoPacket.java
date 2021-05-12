package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.APClient.parts.NetworkPlayer;
import gg.archipelago.APClient.parts.Version;

import java.util.ArrayList;

public class RoomInfoPacket extends APPacket {

    public Version version;

    public String[] tags;

    public boolean password;

    @SerializedName("forfeit_mode")
    public ForfeitMode forfeitMode;

    @SerializedName("remaining_mode")
    public RemainingMode remainingMode;

    @SerializedName("hint_cost")
    public int hintCost;

    @SerializedName("location_check_points")
    public int locationCheckPoints;

    public ArrayList<NetworkPlayer> networkPlayers = new ArrayList<>();

    @SerializedName("datapackage_version")
    public int datapackageVersion;

    @SerializedName("seed_name")
    public String seedName;

    public NetworkPlayer getPlayer(int team, int slot) {
        for(NetworkPlayer player : networkPlayers) {
            if (player.slot == slot && player.team == team) {
                return player;
            }
        }
        return null;
    }
}
