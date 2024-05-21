package dev.koifysh.archipelago.network.client;

import com.google.gson.annotations.SerializedName;
import dev.koifysh.archipelago.Client;
import dev.koifysh.archipelago.events.RetrievedEvent;
import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;

import java.util.Collection;
import java.util.Random;

/**
 * Used to request a single or multiple values from the server's data storage, see the
 * {@link Client#dataStorageSet(SetPacket) ArchipelagoClient.dataStorageSet()} for how to write values to the data storage.
 * A Get package will be answered with a {@link RetrievedEvent RetreivedEvent}.
 */
public class GetPacket extends APPacket {

    /**
     * a list of keys to retrieve data for.
     */
    @SerializedName("keys")
    public Collection<String> keys;

    @SerializedName("request_id")
    private int requestID;

    public GetPacket(Collection<String> keys) {
        super(APPacketType.Get);
        this.keys = keys;
        requestID = new Random().nextInt(Integer.MAX_VALUE);
    }

    public int getRequestID() {
        return requestID;
    }
}
