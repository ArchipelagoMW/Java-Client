package gg.archipelago.client.network.server;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;

public class PrintPacket extends APPacket {

    @SerializedName("text")
    String text;

    public PrintPacket() {
        super(APPacketType.Print);
    }

    public String getText() {
        return text;
    }
}
