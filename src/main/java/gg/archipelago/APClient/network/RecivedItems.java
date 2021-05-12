package gg.archipelago.APClient.network;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.APClient.parts.NetworkItem;

import java.util.ArrayList;

public class RecivedItems extends APPacket {

    @SerializedName("index")
    public int index;

    @SerializedName("items")
    public ArrayList<NetworkItem> items;
}
