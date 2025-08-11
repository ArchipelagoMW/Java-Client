package io.github.archipelagomw;

import io.github.archipelagomw.network.client.LocationChecks;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LocationManager {

    // TODO: why is this field unused?
    private final Client client;
    private WebSocket webSocket;

    private final Set<Long> checkedLocations = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final Set<Long> missingLocations = Collections.newSetFromMap(new ConcurrentHashMap<>());

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

    void setAPWebSocket(WebSocket webSocket) {
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

    public void setMissingLocations(Set<Long> missingLocations) {
        this.missingLocations.clear();
        this.missingLocations.addAll(missingLocations);
    }
}
