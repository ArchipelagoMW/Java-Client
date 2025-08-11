package io.github.archipelagomw.parts;

import com.google.gson.annotations.SerializedName;

public class NetworkSlot {

    @SerializedName("name")
    public String name;

    @SerializedName("game")
    public String game;

    @SerializedName("type")
    public int type;

    public NetworkSlot(String name, String game, int type) {
        this.name = name;
        this.game = game;
        this.type = type;
    }
}
