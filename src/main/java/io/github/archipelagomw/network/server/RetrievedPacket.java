package io.github.archipelagomw.network.server;

import com.google.gson.annotations.SerializedName;
import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;

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

