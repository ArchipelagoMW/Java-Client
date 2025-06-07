package dev.koifysh.archipelago.flags;

/**
 * Flags that will tell you more about the item that was sent.<br>
 * {@link #ADVANCEMENT},
 * {@link #USEFUL},
 * {@link #TRAP}
 */
public class NetworkItem {
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
