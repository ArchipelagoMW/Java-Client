package dev.koifysh.archipelago.network;

import com.google.gson.annotations.SerializedName;

public enum APPacketType {
    @SerializedName("RoomInfo")
    RoomInfo,
    @SerializedName("ConnectionRefused")
    ConnectionRefused,
    @SerializedName("Connected")
    Connected,
    @SerializedName("ReceivedItems")
    ReceivedItems,
    @SerializedName("LocationInfo")
    LocationInfo,
    @SerializedName("RoomUpdate")
    RoomUpdate,
    @SerializedName("Print")
    Print,
    @SerializedName("PrintJSON")
    PrintJSON,
    @SerializedName("DataPackage")
    DataPackage,
    @SerializedName("Connect")
    Connect,
    @SerializedName("LocationChecks")
    LocationChecks,
    @SerializedName("LocationScouts")
    LocationScouts,
    @SerializedName("StatusUpdate")
    StatusUpdate,
    @SerializedName("Say")
    Say,
    @SerializedName("GetDataPackage")
    GetDataPackage,
    @SerializedName("Bounce")
    Bounce,
    @SerializedName("Bounced")
    Bounced,
    @SerializedName("Sync")
    Sync,
    @SerializedName("ConnectUpdate")
    ConnectUpdate,
    @SerializedName("Get")
    Get,
    @SerializedName("Set")
    Set,
    @SerializedName("SetNotify")
    SetNotify,
    @SerializedName("Retrieved")
    Retrieved,
    @SerializedName("SetReply")
    SetReply,
    @SerializedName("InvalidPacket")
    InvalidPacket,
    @SerializedName("UpdateHint")
    UpdateHint
}
