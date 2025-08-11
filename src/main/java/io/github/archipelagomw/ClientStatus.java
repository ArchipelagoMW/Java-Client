package io.github.archipelagomw;

import com.google.gson.annotations.SerializedName;

/**
 * A Status to send to the server. <br>
 * {@link #CLIENT_UNKNOWN} - default, no status. <br>
 * {@link #CLIENT_READY} - Ready to start. <br>
 * {@link #CLIENT_PLAYING} - Player has started playing. <br>
 * {@link #CLIENT_GOAL} - Player has finished their game. This will trigger an auto-release depending on server settings.
 */
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
