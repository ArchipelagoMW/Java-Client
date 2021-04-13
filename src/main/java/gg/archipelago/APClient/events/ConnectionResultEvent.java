package gg.archipelago.APClient.events;

import gg.archipelago.APClient.network.ConnectedPacket;
import gg.archipelago.APClient.network.ConnectionResult;

public class ConnectionResultEvent {

    private boolean canceled = false;
    private final int team;
    private final int slot;
    private final String seedName;
    private final ConnectionResult result;


    public ConnectionResultEvent(ConnectionResult result) {
        this(result,0,0,null);
    }

    public ConnectionResultEvent(ConnectionResult result, int team, int slot, String seedName) {
        this.result = result;
        this.team = team;
        this.slot = slot;
        this.seedName = seedName;
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
}
