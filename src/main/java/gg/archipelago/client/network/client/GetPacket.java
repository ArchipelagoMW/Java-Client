package gg.archipelago.client.network.client;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;

import java.util.Collection;
import java.util.Random;

/**
 * Used to request a single or multiple values from the server's data storage, see the
 * {@link gg.archipelago.client.ArchipelagoClient#dataStorageSet(SetPacket) ArchipelagoClient.dataStorageSet()} for how to write values to the data storage.
 * A Get package will be answered with a {@link gg.archipelago.client.events.RetrievedEvent RetreivedEvent}.
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
