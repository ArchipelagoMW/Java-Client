package dev.koifysh.archipelago;

import com.google.gson.annotations.SerializedName;

public enum ClientStatus {

    @SerializedName("0")
    CLIENT_UNKNOWN(0),
    @SerializedName("10")
    CLIENT_READY(10),
    @SerializedName("20")
    CLIENT_PLAYING(20),
    @SerializedName("30")
    CLIENT_GOAL(30);

    private final int value;
    ClientStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
