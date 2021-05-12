package gg.archipelago.APClient.parts;

import com.google.gson.annotations.SerializedName;

public class NetworkItem {

    @SerializedName("item")
    public int itemID;

    @SerializedName("location")
    public int locationID;

    @SerializedName("player")
    public int playerID;

}
