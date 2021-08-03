package gg.archipelago.APClient.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;

public class BouncePacket extends APPacket {

    @Expose
    @SerializedName("games")
    public String[] games = new String[]{};

    @Expose
    @SerializedName("slots")
    public int[] slots = new int[]{};

    @Expose
    @SerializedName("tags")
    public String[] tags = new String[]{};

    private HashMap<String, Object> data;

    public BouncePacket() {
        super();
        this.cmd = APPacketType.Bounce;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
}
