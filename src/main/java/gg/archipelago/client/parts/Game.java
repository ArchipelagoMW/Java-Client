package gg.archipelago.client.parts;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;

public class Game implements Serializable, Cloneable {

    @SerializedName("version")
    public int version;

    @SerializedName("hash")
    public String hash;

    @SerializedName("item_name_to_id")
    public HashMap<String,Long> itemNameToId = new HashMap<>();

    @SerializedName("location_name_to_id")
    public HashMap<String,Long> locationNameToId = new HashMap<>();

    @Override
    public Game clone() {
        try {
            Game clone = (Game) super.clone();

            clone.version = this.version;
            clone.hash = this.hash;
            clone.itemNameToId = (HashMap<String, Long>) this.itemNameToId.clone();
            clone.locationNameToId = (HashMap<String, Long>) this.locationNameToId.clone();

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
