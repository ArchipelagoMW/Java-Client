package gg.archipelago.APClient.locationmanager;

import gg.archipelago.APClient.APClient;
import gg.archipelago.APClient.APWebSocket;
import gg.archipelago.APClient.DataManager;
import gg.archipelago.APClient.network.LocationChecks;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LocationManager {

    APClient apClient;
    APWebSocket webSocket;

    Set<Integer> checkedLocations = new HashSet<>();

    public LocationManager(APClient apClient) {
        this.apClient = apClient;
    }

    public boolean checkLocation(int id) {
        checkedLocations.add(id);
        apClient.getDataManager().save();
        LocationChecks packet = new LocationChecks();
        packet.locations.add(id);
        if(webSocket != null && webSocket.isAuthenticated()) {
            webSocket.sendPacket(packet);
            return true;
        }
        return false;
    }

    public void writeFromSave(Set<Integer> checkedLocations) {
        this.checkedLocations = checkedLocations;

    }

    public void sendIfChecked(Set<Integer> missingChecks) {
        LocationChecks packet = new LocationChecks();
        packet.locations = new HashSet<>();
        for (int missingCheck : missingChecks) {
            if(checkedLocations.contains(missingCheck)) {
                packet.locations.add(missingCheck);
            }
        }
        if(webSocket != null && !packet.locations.isEmpty())
            webSocket.sendPacket(packet);
    }

    public void sendAllLocations() {
        if (webSocket == null)
                return;
        LocationChecks packet = new LocationChecks();
        packet.locations = checkedLocations;
        webSocket.sendPacket(packet);
    }

    public void setAPWebSocket(APWebSocket apWebSocket) {
        this.webSocket = apWebSocket;
    }

    public Set<Integer> getCheckedLocations() {
        return checkedLocations;
    }

    public void addCheckedLocations(Set<Integer> newLocations) {
        this.checkedLocations.addAll(newLocations);
    }
}
