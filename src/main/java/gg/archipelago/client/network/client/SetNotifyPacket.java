package gg.archipelago.client.network.client;

import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;

import java.util.ArrayList;

public class SetNotifyPacket extends APPacket {

    /**
     * a list of datastorage keys to be notified upon their change.
     */
    public ArrayList<String> keys;
    public SetNotifyPacket(ArrayList<String> keys) {
        this.cmd = APPacketType.Set;
        this.keys = keys;
    }
}
