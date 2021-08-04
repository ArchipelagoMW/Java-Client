package gg.archipelago.APClient.parts;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

public class Game implements Serializable {

    @SerializedName("Name")
    String Name;

    @SerializedName("version")
    int version;

    @SerializedName("item_name_to_id")
    HashMap<String,Integer> itemNameToId = new HashMap<>();

    @SerializedName("location_name_to_id")
    HashMap<String,Integer> locationNameToId = new HashMap<>();
}
