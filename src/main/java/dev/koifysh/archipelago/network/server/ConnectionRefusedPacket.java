package dev.koifysh.archipelago.network.server;

import com.google.gson.annotations.SerializedName;
import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;
import dev.koifysh.archipelago.network.ConnectionResult;

public class ConnectionRefusedPacket extends APPacket {

    @SerializedName("errors")
    public ConnectionResult[] errors;

    public ConnectionRefusedPacket() {
        super(APPacketType.ConnectionRefused);
    }
}
