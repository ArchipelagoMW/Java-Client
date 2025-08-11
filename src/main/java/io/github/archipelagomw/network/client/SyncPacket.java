package io.github.archipelagomw.network.client;

import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;

public class SyncPacket extends APPacket {


    public SyncPacket() {
        super(APPacketType.Sync);
    }
}
