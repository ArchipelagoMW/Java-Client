package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.APClient.parts.NetworkPlayer;
import gg.archipelago.APClient.parts.Version;

public class RoomUpdatePacket extends APPacket {

    public Version version;

    public String[] tags;

    public boolean password;

    @SerializedName("forfeit_mode")
    public ForfeitMode forfeitMode;

    @SerializedName("remaining_mode")
    public RemainingMode remainingMode;

    @SerializedName("hint_cost")
    public int hintCost;

    @SerializedName("hint_points")
    public int hintPoints;

    @SerializedName("location_check_points")
    public int locationCheckPoints;

    public NetworkPlayer[] networkPlayers;

    @SerializedName("datapackage_version")
    public int datapackageVersion;

}
