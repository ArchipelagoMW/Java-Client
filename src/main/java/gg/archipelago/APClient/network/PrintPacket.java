package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;

public class PrintPacket extends APPacket {

    @SerializedName("text")
    String text;

    public String getText() {
        return text;
    }
}
