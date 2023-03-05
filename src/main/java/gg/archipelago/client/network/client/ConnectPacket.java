package gg.archipelago.client.network.client;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;
import gg.archipelago.client.parts.Version;

import java.util.Set;

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

    @SerializedName("items_handling")
    public int itemsHandling;

    @SerializedName("tags")
    public Set<String> tags;

    public ConnectPacket() {
        super(APPacketType.Connect);
    }

}