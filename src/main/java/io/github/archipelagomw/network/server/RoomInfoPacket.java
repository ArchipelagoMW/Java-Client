package io.github.archipelagomw.network.server;

import com.google.gson.annotations.SerializedName;
import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;
import io.github.archipelagomw.parts.NetworkPlayer;
import io.github.archipelagomw.parts.Version;

import java.util.ArrayList;
import java.util.HashMap;

public class RoomInfoPacket extends APPacket {

    public Version version;

    @SerializedName("generator_version")
    public Version generatorVersion;

    public String[] tags;

    public boolean password;

    public HashMap<String, Integer> permissions;

    @SerializedName("hint_cost")
    public int hintCost;

    @SerializedName("location_check_points")
    public int locationCheckPoints;

    @SerializedName("players")
    public ArrayList<NetworkPlayer> networkPlayers = new ArrayList<>();

    @SerializedName("games")
    public ArrayList<String> games = new ArrayList<>();

    @SerializedName("datapackage_checksums")
    public HashMap<String, String> datapackageChecksums = new HashMap<>();

    @SerializedName("seed_name")
    public String seedName;

    @SerializedName("time")
    public double time;

    public RoomInfoPacket() {
        super(APPacketType.RoomInfo);
    }

    public NetworkPlayer getPlayer(int team, int slot) {
        for(NetworkPlayer player : networkPlayers) {
            if (player.slot == slot && player.team == team) {
                return player;
            }
        }
        return null;
    }
}
