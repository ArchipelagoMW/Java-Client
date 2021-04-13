package gg.archipelago.APClient.network;

public class SyncPacket extends APPacket {


    public SyncPacket() {
        this.cmd = APPacketType.Sync;
    }
}
