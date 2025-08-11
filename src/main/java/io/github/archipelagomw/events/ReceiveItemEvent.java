package io.github.archipelagomw.events;

import io.github.archipelagomw.parts.NetworkItem;

public class ReceiveItemEvent implements Event {

    private final NetworkItem item;
    private final int index;
    public ReceiveItemEvent(NetworkItem item, int index) {
        this.item = item;
        this.index = index;
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

    public long getIndex() {
        return index;
    }
}
