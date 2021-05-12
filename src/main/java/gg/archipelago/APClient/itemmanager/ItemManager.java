package gg.archipelago.APClient.itemmanager;

import gg.archipelago.APClient.APClient;
import gg.archipelago.APClient.APWebSocket;
import gg.archipelago.APClient.network.SyncPacket;
import gg.archipelago.APClient.parts.DataPackage;
import gg.archipelago.APClient.parts.NetworkItem;

import java.util.ArrayList;

public class ItemManager {


    APClient apClient;
    APWebSocket webSocket;

    ArrayList<NetworkItem> receivedItems = new ArrayList<>();

    int index;

    public ItemManager(APClient apClient) {
        this.apClient = apClient;
    }

    public void receiveItems(ArrayList<NetworkItem> ids, int index) {
        if (index == 0) {
            receivedItems = new ArrayList<>();
        }
        if (receivedItems.size() == index) {
            receivedItems.addAll(ids);
            DataPackage dp = apClient.getDataPackage();
            int myTeam = apClient.getTeam();
            for (int i = this.index; i < receivedItems.size(); i++) {
                String location = dp.getLocation(receivedItems.get(i).locationID);
                int itemID = receivedItems.get(i).itemID;
                String sendingPlayer = apClient.getRoomInfo().getPlayer(myTeam,receivedItems.get(i).playerID).alias;
                apClient.onReceiveItem(itemID,location,sendingPlayer);
            }

            this.index = receivedItems.size();
            apClient.getDataManager().save();
        }
        else {
            if(webSocket != null) {
                webSocket.sendPacket(new SyncPacket());
                apClient.getLocationManager().sendAllLocations();
            }
        }
    }

    public void writeFromSave(ArrayList<NetworkItem> receivedItems, int index) {
        this.receivedItems = receivedItems;
        this.index = index;
    }

    public void setAPWebSocket(APWebSocket apWebSocket) {
        this.webSocket = apWebSocket;
    }

    public int getIndex() {
        return index;
    }

    public ArrayList<NetworkItem> getReceivedItems() {
        return receivedItems;
    }

    public ArrayList<Integer> getReceivedItemIDs() {
        ArrayList<Integer> ids = new ArrayList<>();
        for (NetworkItem receivedItem : receivedItems) {
            ids.add(receivedItem.itemID);
        }
        return ids;
    }
}
