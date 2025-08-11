package io.github.archipelagomw.network;

import com.google.gson.annotations.SerializedName;

public class APPacket {

    @SerializedName("cmd")
    private APPacketType cmd;

    public APPacket(APPacketType cmd) {
        this.cmd = cmd;
    }

    public APPacketType getCmd() {
        return cmd;
    }
}
