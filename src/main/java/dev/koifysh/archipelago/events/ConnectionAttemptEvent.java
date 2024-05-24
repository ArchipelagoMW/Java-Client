package dev.koifysh.archipelago.events;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.internal.Primitives;

public class ConnectionAttemptEvent implements Event {

    private boolean canceled = false;
    private final int team;
    private final int slot;
    private final String seedName;
    private final JsonElement slot_data;

    public ConnectionAttemptEvent(int team, int slot, String seedName, JsonElement slotData) {
        this.team = team;
        this.slot = slot;
        this.seedName = seedName;
        this.slot_data = slotData;
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

    public <T> T getSlotData(Class<T> classOfT) {
        Object data = new Gson().fromJson(slot_data,classOfT);
        return Primitives.wrap(classOfT).cast(data);
    }

}
