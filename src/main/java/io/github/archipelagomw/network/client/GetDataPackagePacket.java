package io.github.archipelagomw.network.client;

import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;

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
