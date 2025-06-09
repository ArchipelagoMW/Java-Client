package dev.koifysh.archipelago.network.client;

import com.google.gson.annotations.SerializedName;
import dev.koifysh.archipelago.Client;
import dev.koifysh.archipelago.events.RetrievedEvent;
import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Used to request a single or multiple values from the server's data storage, see the
 * {@link Client#dataStorageSet(SetPacket) ArchipelagoClient.dataStorageSet()} for how to write values to the data storage.
 * A Get package will be answered with a {@link RetrievedEvent RetreivedEvent}.
 */
public class GetPacket extends APPacket {

    private static final AtomicInteger requestIdGen = new AtomicInteger();

    /**
     * a list of keys to retrieve data for.
     */
    @SerializedName("keys")
    public Collection<String> keys;

    @SerializedName("request_id")
    private final int requestID;

    public GetPacket(Collection<String> keys) {
        super(APPacketType.Get);
        this.keys = keys;
        requestID = requestIdGen.getAndIncrement();
    }

    public int getRequestID() {
        return requestID;
    }
}
