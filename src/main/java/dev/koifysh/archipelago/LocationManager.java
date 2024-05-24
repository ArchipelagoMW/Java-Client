package dev.koifysh.archipelago;

import dev.koifysh.archipelago.network.client.LocationChecks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LocationManager {

    Client client;
    WebSocket webSocket;

    Set<Long> checkedLocations = new HashSet<>();

    Set<Long> missingLocations = new HashSet<>();

    public LocationManager(Client client) {
        this.client = client;
    }

    public boolean checkLocation(long id) {
        return checkLocations(new ArrayList<Long>(1) {{add(id);}});
    }

    public boolean checkLocations(Collection<Long> ids) {
        ids.removeIf( location -> !missingLocations.contains(location));
        checkedLocations.addAll(ids);
        missingLocations.removeAll(ids);
        LocationChecks packet = new LocationChecks();
        packet.locations.addAll(ids);
        if(webSocket == null)
            return false;

        if(webSocket.isAuthenticated()) {
            webSocket.sendPacket(packet);
            return true;
        }
        return false;
    }

    public void sendIfChecked(Set<Long> missingChecks) {
        LocationChecks packet = new LocationChecks();
        packet.locations = new HashSet<>();
        for (Long missingCheck : missingChecks) {
            if(checkedLocations.contains(missingCheck)) {
                packet.locations.add(missingCheck);
            }
        }
        if(webSocket != null && !packet.locations.isEmpty())
            webSocket.sendPacket(packet);
    }

    public void resendAllCheckedLocations() {
        if (webSocket == null)
                return;
        LocationChecks packet = new LocationChecks();
        packet.locations = checkedLocations;
        webSocket.sendPacket(packet);
    }

    protected void setAPWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public Set<Long> getCheckedLocations() {
        return checkedLocations;
    }

    public Set<Long> getMissingLocations() {
        return missingLocations;
    }

    public void addCheckedLocations(Set<Long> newLocations) {
        this.checkedLocations.addAll(newLocations);
        this.missingLocations.removeAll(newLocations);
    }

    public void setMissingLocations(HashSet<Long> missingLocations) {
        this.missingLocations = missingLocations;
    }
}
