package gg.archipelago.client.network.client;

import gg.archipelago.client.ClientStatus;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;

public class StatusUpdatePacket extends APPacket {

    int status;

    public StatusUpdatePacket(ClientStatus status) {
        super(APPacketType.StatusUpdate);
        this.status = status.getValue();

    }
}
