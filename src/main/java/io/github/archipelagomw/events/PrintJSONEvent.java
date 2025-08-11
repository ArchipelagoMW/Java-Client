package io.github.archipelagomw.events;

import io.github.archipelagomw.Print.APPrint;
import io.github.archipelagomw.Print.APPrintJsonType;
import io.github.archipelagomw.parts.NetworkItem;

/**
 * event that is fired when the server wishes to send a message to the user.
 */
public class PrintJSONEvent implements Event {

    public APPrint apPrint;
    public APPrintJsonType type;
    public int player;
    public NetworkItem item;

    /**
     * @param apPrint list of message segments.
     * @param type the type of the received message.
     * @param player int id of the sending player.
     * @param item the network item that is involved with the message.
     */
    public PrintJSONEvent(APPrint apPrint, APPrintJsonType type, int player, NetworkItem item) {
        this.apPrint = apPrint;
        this.type = type;
        this.player = player;
        this.item = item;
    }
}
