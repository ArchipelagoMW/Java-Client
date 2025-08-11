package io.github.archipelagomw.events;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.Primitives;
import io.github.archipelagomw.network.ConnectionResult;

public class ConnectionResultEvent implements Event {

    private final int team;
    private final int slot;
    private final String seedName;
    private final ConnectionResult result;
    private final JsonElement slot_data;


    public ConnectionResultEvent(ConnectionResult result) {
        this(result,0,0,null,null);
    }

    public ConnectionResultEvent(ConnectionResult result, int team, int slot, String seedName, JsonElement slot_data) {
        this.result = result;
        this.team = team;
        this.slot = slot;
        this.seedName = seedName;
        this.slot_data = slot_data;
    }

    public int getTeam() {
        return team;
    }

    public int getSlot() {
        return slot;
    }

    public String getSeedName() {
        return seedName;
    }

    public ConnectionResult getResult() {
        return result;
    }

    public <T> T getSlotData(Class<T> classOfT) {
        Object data = new Gson().fromJson(slot_data,classOfT);
        return Primitives.wrap(classOfT).cast(data);
    }
}
