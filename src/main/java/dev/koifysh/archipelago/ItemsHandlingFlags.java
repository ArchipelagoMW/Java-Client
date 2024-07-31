package dev.koifysh.archipelago;

/**
 * Item Flag variables to read {@link Client#getItemsHandlingFlags()} and set {@link Client#setItemsHandlingFlags(int)}
 * <br>
 * Current item flags: {@link #SEND_ITEMS} {@link #SEND_OWN_ITEMS} {@link #SEND_STARTING_INVENTORY}
 */
public class ItemsHandlingFlags {

    /**
     * Tells the server to send you items from other worlds.
     */
    public static final int SEND_ITEMS = 0b001;

    /**
     * Tells the server to send your own items to you (remote items game)
     */
    public static final int SEND_OWN_ITEMS = 0b010;

    /**
     * Tells the server to send you any items that You should start with.
     * don't set this if you handle starting items by some kind of data file.
     */
    public static final int SEND_STARTING_INVENTORY = 0b100;

}
