package gg.archipelago.client.network.server;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.ConnectionResult;

public class ConnectionRefusedPacket extends APPacket {

    @SerializedName("errors")
    public ConnectionResult[] errors;
}
