package gg.archipelago.client.parts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.*;

public class DataPackage implements Serializable {

    @Expose
    @SerializedName("games")
    HashMap<String, Game> games = new HashMap<>();

    HashMap<Long, String> itemIdToName = new HashMap<>();

    HashMap<Long, String> locationIdToName = new HashMap<>();

    public String uuid = UUID.randomUUID().toString();

    public String getItem(long itemID, String game) {
        if (!games.containsKey(game))
            return String.format("Unknown Item [%d] for [%s]", itemID, game);

        if(!games.get(game).itemNameToId.containsValue(itemID))
            return String.format("Unknown Item [%d] for [%s]", itemID, game);

        return games.get(game).getItem(itemID);
    }

    public String getLocation(long locationID, String game) {
        if (!games.containsKey(game))
            return String.format("Unknown Location [%d] for [%s]", locationID, game);

        if (!games.get(game).locationNameToId.containsValue(locationID))
            return String.format("Unknown Location [%d] for [%s]", locationID, game);

        return games.get(game).getLocation(locationID);
    }

    public Map<String, String> getChecksums() {
        HashMap<String, String> checksums = new HashMap<>();
        games.forEach((key, value) -> checksums.put(key, value.checksum));
        return checksums;
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
        if(locationIdToName.isEmpty()) {
            for (Map.Entry<String, Game> gameEntry : games.entrySet()) {
                for (Map.Entry<String, Long> locations : gameEntry.getValue().locationNameToId.entrySet()) {
                    itemIdToName.put(locations.getValue(), locations.getKey());
                }
            }
        }
        return itemIdToName;
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


    public String getUUID() {
        return uuid;
    }

    public void update(DataPackage newData) {
        games.putAll(newData.getGames());
    }
}
