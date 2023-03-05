package gg.archipelago.client.network.server;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;

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
