package gg.archipelago.client.network.server;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;

import java.util.Set;

public class ConnectUpdatePacket extends APPacket {

    @SerializedName("tags")
    public Set<String> tags;

    public ConnectUpdatePacket() {
        super(APPacketType.ConnectUpdate);
    }

}