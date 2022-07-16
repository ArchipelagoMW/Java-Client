package gg.archipelago.APClient;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.APClient.parts.NetworkItem;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SaveData {

    @SerializedName("locations")
    public Set<Long> checkedLocations = new HashSet<>();

    @SerializedName("items")
    public ArrayList<NetworkItem> receivedItems = new ArrayList<>();

    @SerializedName("index")
    public int index = 0;

    @SerializedName("id")
    public String id = "";

    @SerializedName("slotid")
    public int slotID = -1;
}
