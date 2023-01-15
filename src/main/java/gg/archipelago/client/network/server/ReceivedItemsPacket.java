package gg.archipelago.client.network.server;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;
import gg.archipelago.client.parts.NetworkItem;

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
