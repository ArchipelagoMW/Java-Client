package dev.koifysh.archipelago.parts;

import com.google.gson.annotations.SerializedName;

public class NetworkItem {

    @SerializedName("item")
    public long itemID;

    @SerializedName("location")
    public long locationID;

    @SerializedName("player")
    public int playerID;

    /**
     * Bit flags that tell you information about the item. bitwise AND them with {@link dev.koifysh.archipelago.flags.NetworkItem} to read.
     */
    @SerializedName("flags")
    public int flags;


    public String itemName;
    public String locationName;
    public String playerName;

}
