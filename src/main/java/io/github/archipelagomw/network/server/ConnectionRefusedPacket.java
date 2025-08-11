package io.github.archipelagomw.network.server;

import com.google.gson.annotations.SerializedName;
import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;
import io.github.archipelagomw.network.ConnectionResult;

public class ConnectionRefusedPacket extends APPacket {

    @SerializedName("errors")
    public ConnectionResult[] errors;

    public ConnectionRefusedPacket() {
        super(APPacketType.ConnectionRefused);
    }
}
