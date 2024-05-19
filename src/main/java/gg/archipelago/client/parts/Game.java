package gg.archipelago.client.parts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.beans.Transient;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Game implements Serializable {



    @SerializedName("checksum")
    public String checksum;

    @SerializedName("item_name_to_id")
    public HashMap<String,Long> itemNameToId = new HashMap<>();

    @SerializedName("location_name_to_id")
    public HashMap<String,Long> locationNameToId = new HashMap<>();

    private final HashMap<Long,String> idToItem = new HashMap<>();
    private final HashMap<Long,String> idToLocation = new HashMap<>();

    public String getItem(long itemID) {
        if(idToItem.isEmpty()) {
            for (Map.Entry<String, Long> entry : itemNameToId.entrySet()) {
                idToItem.put(entry.getValue(), entry.getKey());
            }
        }
        if (!idToItem.containsKey(itemID))
            return String.format("Unknown Item [%d]", itemID);

        return idToItem.get(itemID);
    }

    public String getLocation(long locationID) {
        if(idToLocation.isEmpty()) {
            for (Map.Entry<String, Long> entry : locationNameToId.entrySet()) {
                idToLocation.put(entry.getValue(), entry.getKey());
            }
        }
        if (!idToLocation.containsKey(locationID))
            return String.format("Unknown Location [%d]", locationID);

        return idToLocation.get(locationID);
    }
}
