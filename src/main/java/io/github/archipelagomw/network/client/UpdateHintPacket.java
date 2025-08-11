package io.github.archipelagomw.network.client;

import com.google.gson.annotations.SerializedName;

import io.github.archipelagomw.network.APPacket;
import io.github.archipelagomw.network.APPacketType;

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
