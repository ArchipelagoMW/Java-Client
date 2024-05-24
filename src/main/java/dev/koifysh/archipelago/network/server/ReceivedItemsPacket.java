package dev.koifysh.archipelago.network.server;

import com.google.gson.annotations.SerializedName;
import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;
import dev.koifysh.archipelago.parts.NetworkItem;

import java.util.ArrayList;

public class ReceivedItemsPacket extends APPacket {

    @SerializedName("index")
    public int index;

    @SerializedName("items")
    public ArrayList<NetworkItem> items;

    public ReceivedItemsPacket() {
        super(APPacketType.ReceivedItems);
    }
}
