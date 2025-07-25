package dev.koifysh.archipelago.network.client;

import com.google.gson.annotations.SerializedName;
import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;

import java.util.ArrayList;

public class LocationScouts extends APPacket {

    public ArrayList<Long> locations = new ArrayList<>();
    @SerializedName("create_as_hint")
    public Integer createAsHint;

    public LocationScouts(ArrayList<Long> locations) {
        this(locations, null);
    }

    public LocationScouts(ArrayList<Long> locations, Integer createAsHint) {
        super(APPacketType.LocationScouts);
        this.locations = locations;
        this.createAsHint = createAsHint;
    }
}
