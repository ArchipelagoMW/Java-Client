package gg.archipelago.client;

import gg.archipelago.client.events.ReceiveItemEvent;
import gg.archipelago.client.network.client.SyncPacket;
import gg.archipelago.client.parts.DataPackage;
import gg.archipelago.client.parts.NetworkItem;

import java.util.ArrayList;

public class ItemManager {


    ArchipelagoClient archipelagoClient;
    ArchipelagoWebSocket webSocket;

    ArrayList<NetworkItem> receivedItems = new ArrayList<>();

    int index;

    public ItemManager(ArchipelagoClient archipelagoClient) {
        this.archipelagoClient = archipelagoClient;
    }

    public void receiveItems(ArrayList<NetworkItem> ids, int index) {
        if (index == 0) {
            receivedItems = new ArrayList<>();
        }
        if (receivedItems.size() == index) {
            receivedItems.addAll(ids);
            DataPackage dp = archipelagoClient.getDataPackage();
            int myTeam = archipelagoClient.getTeam();
            for (int i = this.index; i < receivedItems.size(); i++) {
                NetworkItem item = receivedItems.get(i);
                item.itemName = dp.getItem(item.itemID);
                item.locationName = dp.getLocation(item.locationID);
                item.playerName = archipelagoClient.getRoomInfo().getPlayer(myTeam,item.playerID).alias;
                archipelagoClient.getEventManager().callEvent(new ReceiveItemEvent(item, index));
            }

            this.index = receivedItems.size();
        }
        else {
            if(webSocket != null) {
                webSocket.sendPacket(new SyncPacket());
                archipelagoClient.getLocationManager().sendAllLocations();
            }
        }
    }

    public void writeFromSave(ArrayList<NetworkItem> receivedItems, int index) {
        this.receivedItems = receivedItems;
        this.index = index;
    }

    public void setAPWebSocket(ArchipelagoWebSocket archipelagoWebSocket) {
        this.webSocket = archipelagoWebSocket;
    }

    public int getIndex() {
        return index;
    }

    public ArrayList<NetworkItem> getReceivedItems() {
        return receivedItems;
    }

    public ArrayList<Long> getReceivedItemIDs() {
        ArrayList<Long> ids = new ArrayList<>();
        for (NetworkItem receivedItem : receivedItems) {
            ids.add(receivedItem.itemID);
        }
        return ids;
    }
}
