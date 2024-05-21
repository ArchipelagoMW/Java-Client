package dev.koifysh.archipelago;

public class ItemFlags {

    //Indicates you get items sent from other worlds.
    public static final int SEND_ITEMS = 0b001;

    //send your own items to you (remote items game)
    public static final int SEND_OWN_ITEMS = 0b010;

    //send starting inventory upon connect
    public static final int SEND_STARTING_INVENTORY = 0b100;

}
