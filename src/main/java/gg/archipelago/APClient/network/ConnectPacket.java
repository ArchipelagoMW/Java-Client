package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.APClient.parts.Version;

public class ConnectPacket extends APPacket {

    @SerializedName("password")
    public String password;

    @SerializedName("game")
    public String game;

    @SerializedName("name")
    public String name;

    @SerializedName("uuid")
    public String uuid;

    @SerializedName("version")
    public Version version;

    @SerializedName("tags")
    public String[] tags;

    public ConnectPacket() {
        cmd = APPacketType.Connect;
    }

}