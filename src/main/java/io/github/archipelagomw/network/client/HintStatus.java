package io.github.archipelagomw.network.client;

import com.google.gson.annotations.SerializedName;

public enum HintStatus {
    @SerializedName("HINT_UNSPECIFIED")
    HINT_UNSPECIFIED(0),
    @SerializedName("HINT_NO_PRIORITY")
    HINT_NO_PRIORITY(10),
    @SerializedName("HINT_AVOID")
    HINT_AVOID(20),
    @SerializedName("HINT_PRIORITY")
    HINT_PRIORITY(30),
    @SerializedName("HINT_FOUND")
    HINT_FOUND(40);

    public final int value;
    HintStatus(int value) {
        this.value=value;
    }
}