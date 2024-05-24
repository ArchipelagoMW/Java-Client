package dev.koifysh.archipelago.Print;

import com.google.gson.annotations.SerializedName;

public enum APPrintType {
    @SerializedName("text")
    text,
    @SerializedName("player_id")
    playerID,
    @SerializedName("player_name")
    playerName,
    @SerializedName("item_id")
    itemID,
    @SerializedName("item_name")
    itemName,
    @SerializedName("location_id")
    locationID,
    @SerializedName("location_name")
    locationName,
    @SerializedName("entrance_name")
    entranceName,
    @SerializedName("color")
    color

}
