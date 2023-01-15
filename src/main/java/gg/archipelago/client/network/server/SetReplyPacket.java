package gg.archipelago.client.network.server;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.network.APPacket;
import gg.archipelago.client.network.APPacketType;

public class SetReplyPacket extends APPacket {
    @SerializedName("key")
    public String key;
    @SerializedName("value")
    public Object value;
    @SerializedName("original_value")
    public Object original_Value;

    public SetReplyPacket() {
        super(APPacketType.SetReply);
    }
}
