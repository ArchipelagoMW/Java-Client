package dev.koifysh.archipelago.network.server;

import com.google.gson.annotations.SerializedName;
import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;

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
