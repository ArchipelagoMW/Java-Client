package gg.archipelago.APClient.itemmanager;

import gg.archipelago.APClient.APClient;
import gg.archipelago.APClient.APWebSocket;
import gg.archipelago.APClient.network.SyncPacket;
import gg.archipelago.APClient.parts.NetworkItem;

import java.util.ArrayList;

public class ItemManager {


    APClient apClient;
    APWebSocket webSocket;

    ArrayList<Integer> receivedItems = new ArrayList<>();

    int index;

    public ItemManager(APClient apClient) {
        this.apClient = apClient;
    }

    public void receiveItems(NetworkItem[] ids, int index) {
        if (index == 0) {
            receivedItems = new ArrayList<>();
        }
        if (receivedItems.size() == index) {
            for (int i = index; i < index + ids.length; i++) {
                receivedItems.add(ids[i-index].item);
                if(i == this.index) {
                    this.index++;
                    apClient.onReceiveItem(ids[i-index].item, apClient.getDataPackage().getLocation(ids[i-index].location), apClient.getRoomInfo().getPlayer(apClient.getTeam(), ids[i-index].player).alias);
                    apClient.getDataManager().save();
                }
            }
        }
        else {
            if(webSocket != null) {
                webSocket.sendPacket(new SyncPacket());
                apClient.getLocationManager().sendAllLocations();
            }
        }
    }

    public void writeFromSave(ArrayList<Integer> receivedItems, int index) {
        this.receivedItems = receivedItems;
        this.index = index;
    }

    public void setAPWebSocket(APWebSocket apWebSocket) {
        this.webSocket = apWebSocket;
    }

    public int getIndex() {
        return index;
    }

    public ArrayList<Integer> getReceivedItems() {
        return receivedItems;
    }
}
