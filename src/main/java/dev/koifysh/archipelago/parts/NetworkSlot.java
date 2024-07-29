package dev.koifysh.archipelago.parts;

import com.google.gson.annotations.SerializedName;

public class NetworkSlot {

    @SerializedName("name")
    public String name;

    @SerializedName("game")
    public String game;

    @SerializedName("type")
    public int type;


    /**
     * Flags that will tell you more about the slot type.<br>
     * {@link #SPECTATOR},
     * {@link #PLAYER},
     * {@link #GROUP}
     */
    public static class flags {

        /**
         * If set, indicates the slot is a spectator
         */
        public final static int SPECTATOR = 0b001;

        /**
         * If set, indicates the slot is a player
         */
        public final static int PLAYER = 0b010;

        /**
         * If set, indicates the slot is a group.
         */
        public final static int GROUP = 0b100;

    }

    public NetworkSlot(String name, String game, int type) {
        this.name = name;
        this.game = game;
        this.type = type;
    }
}
