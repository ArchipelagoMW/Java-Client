package io.github.archipelagomw.network.client;

import com.google.gson.annotations.SerializedName;
import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;

public class SayPacket extends APPacket {

    @SerializedName("text")
    String text;

    public SayPacket(String message) {
        super(APPacketType.Say);
        text = message;
    }
}
