package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.APClient.parts.NetworkItem;

public class RecivedItems extends APPacket {

    @SerializedName("index")
    public int index;

    @SerializedName("items")
    public NetworkItem[] items;
}
