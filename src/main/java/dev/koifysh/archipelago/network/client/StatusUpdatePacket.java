package dev.koifysh.archipelago.network.client;

import dev.koifysh.archipelago.ClientStatus;
import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;

public class StatusUpdatePacket extends APPacket {

    int status;

    public StatusUpdatePacket(ClientStatus status) {
        super(APPacketType.StatusUpdate);
        this.status = status.getValue();

    }
}
