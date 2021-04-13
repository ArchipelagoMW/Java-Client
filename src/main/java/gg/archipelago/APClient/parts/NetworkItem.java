package gg.archipelago.APClient.parts;

import com.google.gson.annotations.SerializedName;

public class NetworkItem {

    @SerializedName("item")
    public int item;

    @SerializedName("location")
    public int location;

    @SerializedName("player")
    public int player;

}
