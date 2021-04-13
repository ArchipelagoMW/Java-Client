package gg.archipelago.APClient.network;

public class GetDataPackagePacket extends APPacket {

    public GetDataPackagePacket() {
        cmd = APPacketType.GetDataPackage;
    }
}
