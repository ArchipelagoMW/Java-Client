package gg.archipelago.APClient.parts;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.APClient.network.GetDataPackagePacket;

public class NetworkItem {

    @SerializedName("item")
    public long itemID;

    @SerializedName("location")
    public long locationID;

    @SerializedName("player")
    public int playerID;

    public String itemName;
    public String locationName;
    public String playerName;
}
