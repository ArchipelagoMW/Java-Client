package gg.archipelago.client.network.server;

import gg.archipelago.client.Print.APPrintPart;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;

public class JsonPrintPacket extends APPacket {
    APPrintPart[] parts;

    public JsonPrintPacket() {
        super(APPacketType.PrintJSON);
    }
}
