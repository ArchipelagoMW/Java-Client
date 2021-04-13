package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;

public class SayPacket extends APPacket {

    @SerializedName("text")
    String text;

    public SayPacket(String message) {
        cmd = APPacketType.Say;
        text = message;
    }
}
