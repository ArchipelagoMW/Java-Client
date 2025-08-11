package io.github.archipelagomw.network.server;

import io.github.archipelagomw.Print.APPrintPart;
import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;

public class JsonPrintPacket extends APPacket {
    APPrintPart[] parts;

    public JsonPrintPacket() {
        super(APPacketType.PrintJSON);
    }
}
