package gg.archipelago.client.network.client;

import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;

public class SyncPacket extends APPacket {


    public SyncPacket() {
        super(APPacketType.Sync);
    }
}
