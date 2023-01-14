package gg.archipelago.client.events;

import com.google.gson.annotations.SerializedName;

public class SetReplyEvent implements Event {
    @SerializedName("key")
    public String key;
    @SerializedName("value")
    public Object value;
    @SerializedName("original_value")
    public Object original_value;

    public SetReplyEvent(String key, Object value, Object original_value) {
        this.key=key;
        this.value=value;
        this.original_value=original_value;
    }
}
