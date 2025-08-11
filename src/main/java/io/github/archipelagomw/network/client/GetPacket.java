package io.github.archipelagomw.network.client;

import com.google.gson.annotations.SerializedName;
import io.github.archipelagomw.Client;
import io.github.archipelagomw.events.RetrievedEvent;
import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;

import java.util.Collection;
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
