package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.APClient.parts.NetworkPlayer;
import gg.archipelago.APClient.parts.Version;

import java.util.ArrayList;
import java.util.HashMap;

public class RoomUpdatePacket extends APPacket {

    @SerializedName("version")
    public Version version;

    @SerializedName("tags")
    public String[] tags;

    @SerializedName("password")
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

    @SerializedName("checked_locations")
    public ArrayList<Long> checkedLocations = new ArrayList<>();

    @SerializedName("missing_locations")
    public ArrayList<Long> missingLocations = new ArrayList<>();

    @SerializedName("players")
    public ArrayList<NetworkPlayer> networkPlayers = new ArrayList<>();

    @SerializedName("datapackage_version")
    public int datapackageVersion;

    @SerializedName("datapackage_versions")
    public HashMap<String, Integer> datapackageVersions = new HashMap<>();

}
