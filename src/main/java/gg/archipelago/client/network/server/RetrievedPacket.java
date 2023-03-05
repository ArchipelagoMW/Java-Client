package gg.archipelago.client.network.server;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;

import java.util.HashMap;

public class RetrievedPacket  extends APPacket {

    @SerializedName("keys")
    public HashMap<String, Object> keys;

    @SerializedName("request_id")
    public int requestID;

    public RetrievedPacket() {
        super(APPacketType.Retrieved);
    }
}

