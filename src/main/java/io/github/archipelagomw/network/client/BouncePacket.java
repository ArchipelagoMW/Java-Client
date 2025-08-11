package io.github.archipelagomw.network.client;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.github.archipelagomw.events.Event;
import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;

import java.util.HashMap;

public class BouncePacket extends APPacket implements Event {

    @Expose
    @SerializedName("games")
    public String[] games = new String[]{};

    @Expose
    @SerializedName("slots")
    public int[] slots = new int[]{};

    @Expose
    @SerializedName("tags")
    public String[] tags = new String[]{};

    @Expose
    @SerializedName("data")
    private HashMap<String, Object> data;

    public BouncePacket() {
        super(APPacketType.Bounce);
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
}
