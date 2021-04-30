package gg.archipelago.APClient.network;

import gg.archipelago.APClient.ClientStatus;

public class ClientStatusPacket extends APPacket {

    int status;

    public ClientStatusPacket(ClientStatus status) {
        super();
        this.status = status.getValue();
        this.cmd = APPacketType.StatusUpdate;

    }
}
