package gg.archipelago.APClient.Print;

import com.google.gson.annotations.SerializedName;

public enum APPrintType {
    @SerializedName("player_id")
    playerID,
    @SerializedName("item_id")
    itemID,
    @SerializedName("location_id")
    locationID
}
