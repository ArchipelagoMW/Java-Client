package gg.archipelago.APClient.events;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.Primitives;
import gg.archipelago.APClient.SlotData;
import gg.archipelago.APClient.network.ConnectedPacket;
import gg.archipelago.APClient.network.ConnectionResult;

public class ConnectionResultEvent {

    private boolean canceled = false;
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

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCanceled() {
        return canceled;
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
