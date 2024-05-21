package dev.koifysh.archipelago.network.client;

import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;

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
