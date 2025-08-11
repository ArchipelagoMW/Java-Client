package io.github.archipelagomw.network.server;

import com.google.gson.annotations.SerializedName;
import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;

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
