package gg.archipelago.APClient.network;

import java.util.Set;

public class GetDataPackagePacket extends APPacket {

    Set<String> exclusions;

    public GetDataPackagePacket() {
        this(null);
    }

    public GetDataPackagePacket(Set<String> exclusions) {
        cmd = APPacketType.GetDataPackage;
        this.exclusions = exclusions;
    }
}
