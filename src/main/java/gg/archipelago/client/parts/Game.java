package gg.archipelago.client.parts;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

public class Game implements Serializable {

    @SerializedName("version")
    public int version;

    @SerializedName("hash")
    public String hash;

    @SerializedName("item_name_to_id")
    public HashMap<String,Long> itemNameToId = new HashMap<>();

    @SerializedName("location_name_to_id")
    public HashMap<String,Long> locationNameToId = new HashMap<>();
}
