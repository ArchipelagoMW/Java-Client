package io.github.archipelagomw.events;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.HashSet;

public class BouncedEvent implements Event {

    @SerializedName("games")
    public HashSet<String> games;

    @SerializedName("slots")
    public HashSet<Integer> slots;

    @SerializedName("tags")
    public HashSet<String> tags;

    @SerializedName("data")
    private HashMap<String, Object> data;

    public BouncedEvent(HashSet<String> games, HashSet<String> tags, HashSet<Integer> slots, HashMap<String, Object> data) {
        this.games = games;
        this.tags = tags;
        this.slots = slots;
        this.data = data;
    }

    public int getInt(String key) {
        return ((Double)data.get(key)).intValue();
    }

    public float getFloat(String key) {
        return (Float) data.get(key);
    }

    public double getDouble(String key) {
        return (Double) data.get(key);
    }

    public String getString(String key) {
        return (String)data.get(key);
    }

    public boolean getBoolean(String key) {
        return (boolean)data.get(key);
    }

    public Object getObject(String key) {
        return data.get(key);
    }


    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

}
