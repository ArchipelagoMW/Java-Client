package dev.koifysh.archipelago.parts;

import com.google.gson.annotations.SerializedName;
import dev.koifysh.archipelago.NetworkItemFlags;

public class NetworkItem {

    @SerializedName("item")
    public long itemID;

    @SerializedName("location")
    public long locationID;

    @SerializedName("player")
    public int playerID;

    /**
     * Bit flags that tell you information about the item. bitwise AND them with {@link NetworkItemFlags} to read.
     */
    @SerializedName("flags")
    public int flags;


    public String itemName;
    public String locationName;
    public String playerName;

    /**
     * Flags that will tell you more about the item that was sent.<br>
     * {@link #ADVANCEMENT},
     * {@link #USEFUL},
     * {@link #TRAP}
     */
    public static class Flags {

        /**
         * If set, indicates the item can unlock logical advancement
         */
        public final static int ADVANCEMENT = 0b001;

        /**
         * If set, indicates the item is important but not in a way that unlocks advancement
         */
        public final static int USEFUL = 0b010;

        /**
         * If set, indicates the item is a trap
         */
        public final static int TRAP = 0b100;

    }
}
