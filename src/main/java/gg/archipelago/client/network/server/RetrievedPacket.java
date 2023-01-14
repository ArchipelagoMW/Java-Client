package gg.archipelago.client.network.server;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.network.APPacket;

import java.util.HashMap;

public class RetrievedPacket  extends APPacket {

    @SerializedName("keys")
    public HashMap<String, Object> keys;
}

