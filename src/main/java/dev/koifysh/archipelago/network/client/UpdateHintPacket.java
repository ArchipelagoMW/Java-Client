package dev.koifysh.archipelago.network.client;

import com.google.gson.annotations.SerializedName;

import dev.koifysh.archipelago.network.APPacket;
import dev.koifysh.archipelago.network.APPacketType;

public class UpdateHintPacket extends APPacket{
    /*
    * NEED TO IMPLEMENT STUFF FOR UPDATEHINTPACKET?
    */

    @SerializedName("player")
    int player;    
     
    @SerializedName("location")
    int location;

    @SerializedName("status")
    HintStatus status;

    public UpdateHintPacket(){
        super(APPacketType.UpdateHint);
    }
}
