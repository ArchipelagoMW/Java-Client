package gg.archipelago.client.parts;

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
        if(!itemIdToName.containsKey(itemID)) {
            return String.format("Unknown Item [%d]", itemID);
        }

        return itemIdToName.get(itemID);
    }

    public String getLocation(long locationID) {
        if(!locationIdToName.containsKey(locationID))
            return String.format("Unknown Location [%d]", locationID);

        return locationIdToName.get(locationID);
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
        return itemIdToName;
    }

    public HashMap<Long, String> getItemsForGame(String game) {
        HashMap<Long, String> ret = new HashMap<>();
        for (Map.Entry<String, Game> gameEntry : games.entrySet()) {
            if(!gameEntry.getKey().equals(game)) continue;
            for (Map.Entry<String, Long> items : gameEntry.getValue().itemNameToId.entrySet()) {
                ret.put(items.getValue(), items.getKey());
            }
        }
        return ret;
    }

    public HashMap<Long, String> getLocations() {
        return locationIdToName;
    }

    public HashMap<Long, String> getLocationsForGame(String game) {
        HashMap<Long, String> ret = new HashMap<>();
        for (Map.Entry<String, Game> gameEntry : games.entrySet()) {
            if(!gameEntry.getKey().equals(game)) continue;
            for (Map.Entry<String, Long> locations : gameEntry.getValue().locationNameToId.entrySet()) {
                ret.put(locations.getValue(), locations.getKey());
            }
        }
        return ret;
    }

    public int getVersion() {
        return version;
    }

    public String getUUID() {
        return uuid;
    }

    public void update(DataPackage newData) {
        games.putAll(newData.getGames());
        buildItemsMap();
        buildLocationsMap();
        version = newData.version;
    }

    private void buildItemsMap() {
        for (Map.Entry<String, Game> gameEntry : games.entrySet()) {
            for (Map.Entry<String, Long> items : gameEntry.getValue().itemNameToId.entrySet()) {
                itemIdToName.put(items.getValue(), items.getKey());
            }
        }
    }

    private void buildLocationsMap() {
        locationIdToName.clear();

        for (Map.Entry<String, Game> gameEntry : games.entrySet()) {
            for (Map.Entry<String, Long> locations : gameEntry.getValue().locationNameToId.entrySet()) {
                locationIdToName.put(locations.getValue(), locations.getKey());
            }
        }
    }
}
