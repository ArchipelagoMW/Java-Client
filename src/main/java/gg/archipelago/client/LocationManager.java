package gg.archipelago.client;

import gg.archipelago.client.network.client.LocationChecks;

import java.util.*;

public class LocationManager {

    ArchipelagoClient archipelagoClient;
    ArchipelagoWebSocket webSocket;

    Set<Long> checkedLocations = new HashSet<>();

    Set<Long> missingLocations = new HashSet<>();

    public LocationManager(ArchipelagoClient archipelagoClient) {
        this.archipelagoClient = archipelagoClient;
    }

    public boolean checkLocation(long id) {
        return checkLocations(Collections.singletonList(id));
    }

    public boolean checkLocations(Collection<Long> ids) {
        ids.removeIf( location -> !missingLocations.contains(location));
        checkedLocations.addAll(ids);
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

    protected void setAPWebSocket(ArchipelagoWebSocket archipelagoWebSocket) {
        this.webSocket = archipelagoWebSocket;
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
