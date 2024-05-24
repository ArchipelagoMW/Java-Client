package dev.koifysh.archipelago.network.server;

import com.google.gson.annotations.SerializedName;
import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;

public class InvalidPacket extends APPacket {

    @SerializedName("type")
    public String type;
    @SerializedName("original_cmd")
    public String Original_cmd;
    @SerializedName("text")
    public String text;

    public InvalidPacket() {
        super(APPacketType.InvalidPacket);
    }
}
