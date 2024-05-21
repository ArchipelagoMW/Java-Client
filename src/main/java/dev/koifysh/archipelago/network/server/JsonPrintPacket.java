package dev.koifysh.archipelago.network.server;

import dev.koifysh.archipelago.Print.APPrintPart;
import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;

public class JsonPrintPacket extends APPacket {
    APPrintPart[] parts;

    public JsonPrintPacket() {
        super(APPacketType.PrintJSON);
    }
}
