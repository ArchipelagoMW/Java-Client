package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.APClient.parts.Version;

public class ConnectUpdatePacket extends APPacket {

    @SerializedName("tags")
    public String[] tags;

    public ConnectUpdatePacket() {
        cmd = APPacketType.ConnectUpdate;
    }

}