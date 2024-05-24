package dev.koifysh.archipelago.network.server;

import com.google.gson.annotations.SerializedName;
import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;

import java.util.HashMap;
import java.util.HashSet;

public class BouncedPacket extends APPacket {

    @SerializedName("games")
    public HashSet<String> games = new HashSet<>();

    @SerializedName("slots")
    public HashSet<Integer> slots = new HashSet<>();

    @SerializedName("tags")
    public HashSet<String> tags = new HashSet<>();

    @SerializedName("data")
    public final HashMap<String, Object> data = new HashMap<>();

    public BouncedPacket() {
        super(APPacketType.Bounced);
    }
}
