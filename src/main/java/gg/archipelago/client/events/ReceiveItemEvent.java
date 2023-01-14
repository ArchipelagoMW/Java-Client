package gg.archipelago.client.events;

import gg.archipelago.client.parts.NetworkItem;

public class ReceiveItemEvent implements Event {

    private final NetworkItem item;
    public ReceiveItemEvent(NetworkItem item) {
        this.item = item;
    }

    public NetworkItem getItem() {
        return item;
    }

    public String getItemName() {
        return item.itemName;
    }

    public String getLocationName() {
        return item.locationName;
    }

    public String getPlayerName() {
        return item.playerName;
    }

    public Long getItemID() {
        return item.itemID;
    }

    public Long getLocationID() {
        return item.locationID;
    }

    public int getPlayerID() {
        return item.playerID;
    }
}
