package gg.archipelago.client.network.client;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;

public class SayPacket extends APPacket {

    @SerializedName("text")
    String text;

    public SayPacket(String message) {
        super(APPacketType.Say);
        text = message;
    }
}
