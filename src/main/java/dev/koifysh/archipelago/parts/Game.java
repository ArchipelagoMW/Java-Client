package dev.koifysh.archipelago.parts;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Game implements Serializable {



    @SerializedName("checksum")
    public String checksum;

    @SerializedName("item_name_to_id")
    public Map<String,Long> itemNameToId = new HashMap<>();

    @SerializedName("location_name_to_id")
    public Map<String,Long> locationNameToId = new HashMap<>();

    private final Map<Long,String> idToItem = new ConcurrentHashMap<>();
    private final Map<Long,String> idToLocation = new ConcurrentHashMap<>();

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
