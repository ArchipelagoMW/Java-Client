package io.github.archipelagomw.Print;

import com.google.gson.annotations.SerializedName;

public enum APPrintJsonType {

    /**
     * A player received an item.
     */
    @SerializedName("ItemSend")
    ItemSend,

    /**
     * A player used the `!getitem` command.
     */
    @SerializedName("ItemCheat")
    ItemCheat,

    /**
     * A player hinted.
     */
    @SerializedName("Hint")
    Hint,

    /**
     * A player connected.
     */
    @SerializedName("Join")
    Join,

    /**
     * A player disconnected.
     */
    @SerializedName("Part")
    Part,

    /**
     * A player sent a chat message.
     */
    @SerializedName("Chat")
    Chat,

    /**
     * The server broadcast a message.
     */
    @SerializedName("ServerChat")
    ServerChat,

    /**
     * The client has triggered a tutorial message, such as when first connecting.
     */
    @SerializedName("entrance_name")
    Tutorial,

    /**
     * A player changed their tags.
     */
    @SerializedName("TagsChanged")
    TagsChanged,

    /**
     *  	Someone (usually the client) entered an ! command.
     */
    @SerializedName("CommandResult")
    CommandResult,

    /**
     * The client entered an !admin command.
     */
    @SerializedName("AdminCommandResult")
    AdminCommandResult,

    /**
     * A player reached their goal.
     */
    @SerializedName("Goal")
    Goal,

    /**
     * A player released the remaining items in their world.
     */
    @SerializedName("Release")
    Release,

    /**
     * A player collected the remaining items for their world.
     */
    @SerializedName("Collect")
    Collect,

    /**
     * The current server countdown has progressed.
     */
    @SerializedName("Countdown")
    Countdown,

    /**
     * The Message type was not set, or is invalid.
     */
    Unknown

}
