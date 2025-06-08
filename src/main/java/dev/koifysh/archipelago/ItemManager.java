package dev.koifysh.archipelago;

import dev.koifysh.archipelago.events.ReceiveItemEvent;
import dev.koifysh.archipelago.network.client.SyncPacket;
import dev.koifysh.archipelago.parts.DataPackage;
import dev.koifysh.archipelago.parts.NetworkItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemManager {


    private final Client client;
    private WebSocket webSocket;

    private List<NetworkItem> receivedItems = new ArrayList<>();

    private final AtomicInteger index = new AtomicInteger();

    public ItemManager(Client client) {
        this.client = client;
    }

    public void receiveItems(List<NetworkItem> ids, int index) {
        if (index == 0) {
            receivedItems = new ArrayList<>();
        }
        if (receivedItems.size() == index) {
            synchronized (this) {
                receivedItems.addAll(ids);
            }
            DataPackage dp = client.getDataPackage();
            int myTeam = client.getTeam();
            for (int i = this.index.get(); i < receivedItems.size(); i++) {
                NetworkItem item = receivedItems.get(i);
                item.itemName = dp.getItem(item.itemID, client.getGame());
                item.locationName = dp.getLocation(item.locationID, client.getSlotInfo().get(item.playerID).game);
                item.playerName = client.getRoomInfo().getPlayer(myTeam,item.playerID).alias;
                client.getEventManager().callEvent(new ReceiveItemEvent(item, i+1));
            }

            this.index.set(receivedItems.size());
        }
        else {
            if(webSocket != null) {
                webSocket.sendPacket(new SyncPacket());
                client.getLocationManager().resendAllCheckedLocations();
            }
        }
    }

    public void writeFromSave(List<NetworkItem> receivedItems, int index) {
        this.receivedItems = new ArrayList<>(receivedItems);
        this.index.set(index);
    }

    void setAPWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public int getIndex() {
        return index.get();
    }

    public List<NetworkItem> getReceivedItems() {
        synchronized (this) {
            return new ArrayList<>(receivedItems);
        }
    }

    public List<Long> getReceivedItemIDs() {
        List<Long> ids = new ArrayList<>();
        synchronized (this) {
            for (NetworkItem receivedItem : receivedItems) {
                ids.add(receivedItem.itemID);
            }
        }
        return ids;
    }
}
