package gg.archipelago.APClient.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LazilyParsedNumber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BouncedPacket extends APPacket {

    @SerializedName("games")
    public HashSet<String> games = new HashSet<>();

    @SerializedName("slots")
    public HashSet<Integer> slots = new HashSet<>();

    @SerializedName("tags")
    public HashSet<String> tags = new HashSet<>();

    private final HashMap<String, Object> data = new HashMap<>();

    public BouncedPacket() {
        super();
        this.cmd = APPacketType.Bounced;
    }

    public void setData(JsonObject data){
        for (Map.Entry<String, JsonElement> entry : data.entrySet()) {
            JsonPrimitive jp = entry.getValue().getAsJsonPrimitive();
            if (jp.isBoolean()) {
                this.data.put(entry.getKey(),jp.getAsBoolean());
            }
            else if (jp.isNumber()) {
                this.data.put(entry.getKey(),jp.getAsNumber());
            }
            else if (jp.isString()) {
                this.data.put(entry.getKey(),jp.getAsString());
            }
        }
    }

    public int getInt(String key) {
        return ((LazilyParsedNumber)data.get(key)).intValue();
    }

    public float getFloat(String key) {
        return ((LazilyParsedNumber)data.get(key)).floatValue();
    }

    public double getDouble(String key) {
        return ((LazilyParsedNumber)data.get(key)).doubleValue();
    }

    public String getString(String key) {
        return (String)data.get(key);
    }

    public boolean getBoolean(String key) {
        return (boolean)data.get(key);
    }
}
