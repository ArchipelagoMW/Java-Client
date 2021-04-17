package gg.archipelago.APClient.parts;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.UUID;

public class DataPackage implements Serializable {

    @SerializedName("lookup_any_location_id_to_name")
    Hashtable<Integer, String> locations = new Hashtable<>();
    @SerializedName("lookup_any_item_id_to_name")
    Hashtable<Integer, String> items = new Hashtable<>();
    @SerializedName("version")
    int version;

    public String uuid = UUID.randomUUID().toString();

    public int getVersion() {
        return version;
    }

    public Hashtable<Integer, String> getItems() {
        return items;
    }

    public Hashtable<Integer, String> getLocations() {
        return locations;
    }

    public DataPackage() {
        version = -1;
    }

    public String getItem(int itemID) {
        String item = String.format("Unknown Item [%d]", itemID);
        if (items.containsKey(itemID)) {
            item = items.get(itemID);
        }
        return item;
    }

    public String getLocation(int locationID) {
        String location = String.format("Unknown Location [%d]", locationID);
        if (locations.containsKey(locationID)) {
            location = locations.get(locationID);
        }
        return location;
    }

    public String getUUID() {
        return uuid;
    }
}
