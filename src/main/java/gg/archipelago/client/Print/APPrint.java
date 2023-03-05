package gg.archipelago.client.Print;

import com.google.gson.annotations.SerializedName;
import gg.archipelago.client.parts.NetworkItem;

public class APPrint {

    @SerializedName("data")
    public APPrintPart[] parts;

    @SerializedName("type")
    public String type;

    @SerializedName("receiving")
    public int receiving;

    @SerializedName("item")
    public NetworkItem item;

    @SerializedName("found")
    public boolean found;

}
