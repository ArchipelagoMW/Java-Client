package gg.archipelago.client.network.client;

import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;

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
