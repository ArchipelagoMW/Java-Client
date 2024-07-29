package dev.koifysh.archipelago.Print;

import com.google.gson.annotations.SerializedName;
import dev.koifysh.archipelago.parts.NetworkItem;

public class APPrint {

    @SerializedName("data")
    public APPrintPart[] parts;

    @SerializedName("type")
    public APPrintJsonType type;

    @SerializedName("receiving")
    public int receiving;

    @SerializedName("item")
    public NetworkItem item;

    @SerializedName("found")
    public boolean found;

    @SerializedName("team")
    public String team;

    @SerializedName("slot")
    public int slot;

    @SerializedName("message")
    public String message;

    @SerializedName("tags")
    public String[] tags;

    @SerializedName("countdown")
    public int countdown;

}
