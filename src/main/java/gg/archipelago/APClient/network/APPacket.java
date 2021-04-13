package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;

public class APPacket {

    @SerializedName("cmd")
    public APPacketType cmd;

}
