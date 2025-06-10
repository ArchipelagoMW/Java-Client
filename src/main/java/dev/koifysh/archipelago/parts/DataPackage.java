package dev.koifysh.archipelago.parts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataPackage implements Serializable {

    @Expose
    @SerializedName("games")
    HashMap<String, Game> games = new HashMap<>();

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

    public Game getGame(String game) {
        return games.get(game);
    }


    public String getUUID() {
        return uuid;
    }

    public void update(DataPackage newData) {
        games.putAll(newData.getGames());
    }

    public void update(String name, Game game)
    {
        games.put(name, game);
    }
}
