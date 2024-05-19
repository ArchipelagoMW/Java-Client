package gg.archipelago.client.network.client;

import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;

import java.util.Set;

public class GetDataPackagePacket extends APPacket {

    Set<String> games;

    public GetDataPackagePacket() {
        this(null);
    }

    public GetDataPackagePacket(Set<String> games) {
        super(APPacketType.GetDataPackage);
        this.games = games;
    }
}
