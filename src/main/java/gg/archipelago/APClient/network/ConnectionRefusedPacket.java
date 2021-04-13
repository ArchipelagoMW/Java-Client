package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;

public class ConnectionRefusedPacket extends APPacket {

    @SerializedName("errors")
    public ConnectionResult[] errors;
}
