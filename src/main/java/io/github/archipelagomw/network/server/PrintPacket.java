package io.github.archipelagomw.network.server;

import com.google.gson.annotations.SerializedName;
import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;

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
