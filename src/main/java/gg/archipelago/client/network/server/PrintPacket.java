package gg.archipelago.client.network.server;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.network.APPacket;

public class PrintPacket extends APPacket {

    @SerializedName("text")
    String text;

    public String getText() {
        return text;
    }
}
