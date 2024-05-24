package dev.koifysh.archipelago.network.client;

import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;

import java.util.Collection;

public class SetNotifyPacket extends APPacket {

    /**
     * a list of datastorage keys to be notified upon their change.
     */
    public Collection<String> keys;

    public SetNotifyPacket(Collection<String> keys) {
        super(APPacketType.SetNotify);
        this.keys = keys;
    }
}
