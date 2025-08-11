package io.github.archipelagomw.network.server;

import com.google.gson.annotations.SerializedName;
import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;

import java.util.Set;

public class ConnectUpdatePacket extends APPacket {

    @SerializedName("tags")
    public Set<String> tags;

    public ConnectUpdatePacket() {
        super(APPacketType.ConnectUpdate);
    }

}