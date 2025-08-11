package io.github.archipelagomw.network.client;

import io.github.archipelagomw.ClientStatus;
import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;

public class StatusUpdatePacket extends APPacket {

    int status;

    public StatusUpdatePacket(ClientStatus status) {
        super(APPacketType.StatusUpdate);
        this.status = status.getValue();

    }
}
