package gg.archipelago.APClient.parts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.*;

public class DataPackage implements Serializable {

    @Expose
    @SerializedName("games")
    HashMap<String, Game> games = new HashMap<>();

    @Expose
    @SerializedName("version")
    int version = -1;

    HashMap<Long, String> itemIdToName = new HashMap<>();

    HashMap<Long, String> locationIdToName = new HashMap<>();

    public String uuid = UUID.randomUUID().toString();

    public String getItem(long itemID) {
        for (Map.Entry<String, Game> game : games.entrySet()) {
            for (Map.Entry<String, Long> item : game.getValue().itemNameToId.entrySet()) {
                if(item.getValue() == itemID)
                    return item.getKey();
            }
        }
        return String.format("Unknown Item [%d]", itemID);
    }

    public String getLocation(long locationID) {
        if(locationIdToName.containsKey(locationID))
            return locationIdToName.get(locationID);
        for (Map.Entry<String, Game> game : games.entrySet()) {
            for (Map.Entry<String, Long> location : game.getValue().locationNameToId.entrySet()) {
                if(location.getValue() == locationID) {
                    locationIdToName.put(locationID, location.getKey());
                    return location.getKey();
                }
            }
        }
        return String.format("Unknown Location [%d]", locationID);
    }

    public Map<String, Integer> getVersions() {
        HashMap<String, Integer> versions = new HashMap<>();
        games.forEach((key, value) -> versions.put(key, value.version));
        return versions;
    }

    public HashMap<String, Game> getGames() {
        return games;
    }

    public HashMap<Long, String> getItems() {
        if(itemIdToName.isEmpty()) {
            for (Map.Entry<String, Game> gameEntry : games.entrySet()) {
                for (Map.Entry<String, Long> items : gameEntry.getValue().itemNameToId.entrySet()) {
                    itemIdToName.put(items.getValue(), items.getKey());
                }
            }
        }
        return itemIdToName;
    }

    public HashMap<Long, String> getLocations() {
        if(locationIdToName.isEmpty()) {
            for (Map.Entry<String, Game> gameEntry : games.entrySet()) {
                for (Map.Entry<String, Long> items : gameEntry.getValue().locationNameToId.entrySet()) {
                    itemIdToName.put(items.getValue(), items.getKey());
                }
            }
        }
        return itemIdToName;
    }

    public int getVersion() {
        return version;
    }

    public String getUUID() {
        return uuid;
    }

    public void update(DataPackage newData) {
        games.putAll(newData.getGames());
        version = newData.version;
    }
}
