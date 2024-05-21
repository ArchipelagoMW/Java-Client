package dev.koifysh.archipelago.network.client;

import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;

public class SyncPacket extends APPacket {


    public SyncPacket() {
        super(APPacketType.Sync);
    }
}
