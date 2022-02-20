package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.APClient.parts.Version;

public class ConnectPacket extends APPacket {

    //Indicates you get items sent from other worlds.
    public static final int SEND_ITEMS = 0b001;

    //send your own items to you (remote items game)
    public static final int SEND_OWN_ITEMS = 0b010;

    //send starting inventory upon connect
    public static final int SEND_STARTING_INVENTORY = 0b100;

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
    public String[] tags;

    public ConnectPacket() {
        cmd = APPacketType.Connect;
    }

}