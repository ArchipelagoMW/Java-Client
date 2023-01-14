package gg.archipelago.client.parts;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

public class Game implements Serializable {

    @SerializedName("version")
    int version;

    @SerializedName("item_name_to_id")
    HashMap<String,Long> itemNameToId = new HashMap<>();

    @SerializedName("location_name_to_id")
    HashMap<String,Long> locationNameToId = new HashMap<>();
}
