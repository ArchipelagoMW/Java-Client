package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.HashSet;

public class BouncedPacket extends APPacket {

    @SerializedName("games")
    public HashSet<String> games = new HashSet<>();

    @SerializedName("slots")
    public HashSet<Integer> slots = new HashSet<>();

    @SerializedName("tags")
    public HashSet<String> tags = new HashSet<>();

    @SerializedName("data")
    private final HashMap<String, String> data = new HashMap<>();

    public BouncedPacket() {
        super();
        this.cmd = APPacketType.Bounced;
    }

    public int getInt(String key) {
        return Integer.parseInt(data.get(key));
    }

    public float getFloat(String key) {
        return Float.parseFloat(data.get(key));
    }

    public double getDouble(String key) {
        return Double.parseDouble(data.get(key));
    }

    public String getString(String key) {
        return data.get(key);
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(data.get(key));
    }

    public boolean containsKey(String key) {
        return data.containsKey(key);
    }
}
